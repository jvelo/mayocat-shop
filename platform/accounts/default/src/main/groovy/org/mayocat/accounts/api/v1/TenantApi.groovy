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
import org.mayocat.Slugifier
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.model.Tenant
import org.mayocat.attachment.MetadataExtractor
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityDataLoader
import org.mayocat.image.model.Image
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Attachment
import org.mayocat.model.Entity
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.ImageGalleryApiObject
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.EntityListStore
import org.mayocat.store.InvalidEntityException
import org.mayocat.theme.ThemeDefinition
import org.slf4j.Logger
import org.xwiki.component.annotation.Component
import org.xwiki.component.phase.Initializable

import javax.inject.Inject
import javax.inject.Provider
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
@Component("/api/tenant")
@Path("/api/tenant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class TenantApi implements Resource, Initializable {

    @Inject
    WebContext context

    @Inject
    EntityDataLoader dataLoader

    @Inject
    AccountsService accountsService

    @Inject
    GeneralSettings generalSettings

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    Map<String, MetadataExtractor> extractors

    @Inject
    PlatformSettings platformSettings

    @Inject
    Slugifier slugifier

    @Inject
    Logger logger

    EntityApiDelegateHandler tenantHandler = new EntityApiDelegateHandler() {
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

    AttachmentApiDelegate attachmentApi

    ImageGalleryApiDelegate imageGalleryApi

    void initialize()
    {
        attachmentApi = new AttachmentApiDelegate([
                extractors            : extractors,
                attachmentStore       : attachmentStore,
                slugifier             : slugifier,
                handler               : tenantHandler,
                doAfterAttachmentAdded: { String target, Entity entity, String fileName, Attachment created ->
                    switch (target) {
                        case "logo":
                            def tenant = entity as Tenant
                            imageGalleryApi.afterImageAddedToGallery(tenant, fileName, created)
                            tenant.featuredImageId = created.id;
                            tenantHandler.updateEntity(entity);
                    }
                }
        ])
        imageGalleryApi = new ImageGalleryApiDelegate([
                dataLoader     : dataLoader,
                attachmentStore: attachmentStore.get(),
                entityListStore: entityListStore.get(),
                handler        : tenantHandler
        ])
    }

    @GET
    @Authorized
    @ExistingTenant
    def currentTenant()
    {
        Tenant tenant = context.tenant
        def tenantData = dataLoader.load(tenant)
        TenantApiObject tenantApiObject = new TenantApiObject([
                _href: "/api/tenant/"
        ])
        tenantApiObject.withEmbeddedImages(tenantData.getDataList(Image.class), tenant.featuredImageId)
        tenantApiObject.withTenant(tenant, globalTimeZone)
        tenant

        if (tenant.addons.isLoaded()) {
            tenantApiObject.withAddons(tenant.addons.get())
        }
        tenantApiObject
    }

    @POST
    @Authorized
    @ExistingTenant
    public Response updateTenant(TenantApiObject tenantRepresentation) {
        Tenant currentTenant = context.tenant

        Tenant tenant = tenantRepresentation.toTenant(platformSettings,
                Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

        // Forbid to change slug
        tenant.id = currentTenant.id
        tenant.slug = currentTenant.slug

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
    def addAttachment(@FormDataParam("file") InputStream uploadedInputStream,
                      @FormDataParam("file") FormDataContentDisposition fileDetail,
                      @FormDataParam("filename") String sentFilename,
                      @FormDataParam("title") String title,
                      @FormDataParam("description") String description,
                      @FormDataParam("target") String target,
                      @Context UriInfo uriInfo)
    {
        attachmentApi.addAttachment(context.tenant.slug, uploadedInputStream, fileDetail, sentFilename, title, description, target, uriInfo)
    }

    @Path("images")
    @GET
    List<ImageApiObject> getImages()
    {
        imageGalleryApi.getImages(context.tenant.slug)
    }

    @Path("images/{imageSlug}")
    @Authorized
    @POST
    @Consumes(MediaType.WILDCARD)
    def updateImage(@PathParam("imageSlug") String imageSlug, ImageApiObject image)
    {
        imageGalleryApi.updateImage(context.tenant.slug, imageSlug, image)
    }

    @Path("images/{imageSlug}")
    @Authorized
    @DELETE
    @Consumes(MediaType.WILDCARD)
    def detachImage(@PathParam("slug") String slug,
                    @PathParam("imageSlug") String imageSlug)
    {
        imageGalleryApi.detachImage(context.tenant.slug, imageSlug)
    }

    @Path("images/")
    @Authorized
    @POST
    def updateImageGallery(ImageGalleryApiObject gallery)
    {
        imageGalleryApi.updateImageGallery(context.tenant.slug, gallery)
    }
}
