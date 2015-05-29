/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import com.google.common.base.Optional
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.model.Tenant
import org.mayocat.attachment.model.Attachment
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.model.Entity
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.ImageGalleryApiObject
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.InvalidEntityException
import org.mayocat.theme.ThemeDefinition
import org.slf4j.Logger
import org.xwiki.component.annotation.Component
import org.xwiki.component.phase.Initializable

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * This resource allow access to the current tenant information. For complete tenant management API, see the
 * TenantManagerResource in the manager module.
 *
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/tenant")
@Path("/tenant/{tenant}/api/tenant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class TenantApi implements Resource, AttachmentApiDelegate, ImageGalleryApiDelegate
{
    @Inject
    AccountsService accountsService

    @Inject
    GeneralSettings generalSettings

    @Inject
    PlatformSettings platformSettings

    @Inject
    Logger logger

    EntityApiDelegateHandler handler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return accountsService.findTenant(slug)
        }

        void updateEntity(Entity tenant)
        {
            accountsService.updateTenant(tenant as Tenant)
        }

        String type()
        {
            "tenant"
        }
    }

    Closure doAfterAttachmentAdded = { String target, Entity entity, String fileName, Attachment created ->
        switch (target) {
            case "image-gallery":
                afterImageAddedToGallery(entity as Tenant, fileName, created)
                break;
            case "logo":
                def tenant = entity as Tenant
                afterImageAddedToGallery(tenant, fileName, created)
                tenant.featuredImageId = created.id;
                handler.updateEntity(entity);
        }
    }

    @GET
    @Authorized
    @ExistingTenant
    def currentTenant()
    {
        Tenant tenant = context.tenant
        def tenantData =  this.dataLoader.load(tenant)
        TenantApiObject tenantApiObject = new TenantApiObject([
                _href : "/api/tenant/",
                _links: [
                        self  : new LinkApiObject([href: "${context.request.tenantPrefix}/api/tenant/"]),
                        images: new LinkApiObject([href: "${context.request.tenantPrefix}/api/tenant/images"])
                ]
        ])

        def gallery = tenantData.getData(ImageGallery.class)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        tenantApiObject.withEmbeddedImages(images, tenant.featuredImageId, context.request.tenantPrefix)
        tenantApiObject.withTenant(tenant, globalTimeZone)

        if (tenant.addons.isLoaded()) {
            tenantApiObject.withAddons(tenant.addons.get())
        }
        tenantApiObject
    }

    @POST
    @Authorized
    @ExistingTenant
    public Response updateTenant(TenantApiObject tenantRepresentation)
    {
        Tenant currentTenant = context.tenant

        Tenant tenant = tenantRepresentation.toTenant(platformSettings,
                Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

        // Forbid to change slug, id, creationDate and defaultHost
        tenant.id = currentTenant.id
        tenant.slug = currentTenant.slug
        tenant.defaultHost = currentTenant.defaultHost
        tenant.creationDate = currentTenant.creationDate

        // Featured image is updated via the /images API only, set it back
        tenant.featuredImageId = currentTenant.featuredImageId

        try {
            this.accountsService.updateTenant(tenant)
            return Response.ok().build()
        } catch (InvalidEntityException e) {
            return Response.status(422).entity("Invalid entity").build()
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.time.timeZone.defaultValue)
    }

    // Delegate to attachments and images API delegates, but without their {{slug}} prefixes (meant for product, pages, etc.)
    @Path("attachments")
    @Authorized
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    def addAttachment(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("target") String target,
            @Context UriInfo uriInfo)
    {
        addAttachment(context.tenant.slug, uploadedInputStream, fileDetail, sentFilename, title, description, target,
                uriInfo)
    }

    @Path("images")
    @GET
    def List<ImageApiObject> getImages()
    {
        getImages(context.tenant.slug)
    }

    @Path("images/{imageSlug}")
    @Authorized
    @POST
    @Consumes(MediaType.WILDCARD)
    def Response updateImage(@PathParam("imageSlug") String imageSlug, ImageApiObject image)
    {
        ImageGalleryApiDelegate.super.updateImage(imageSlug, image)
    }

    @Path("images/{imageSlug}")
    @Authorized
    @DELETE
    @Consumes(MediaType.WILDCARD)
    def Response detachImage(@PathParam("imageSlug") String imageSlug)
    {
        detachImage(context.tenant.slug, imageSlug)
    }

    @Path("images/")
    @Authorized
    @POST
    def void updateImageGallery(ImageGalleryApiObject gallery)
    {
        updateImageGallery(context.tenant.slug, gallery)
    }
}
