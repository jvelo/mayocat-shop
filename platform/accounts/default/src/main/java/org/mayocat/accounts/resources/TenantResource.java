/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;
import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.representations.TenantRepresentation;
import org.mayocat.attachment.util.AttachmentUtils;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.rest.resources.AbstractAttachmentResource;
import org.mayocat.rest.support.AddonsRepresentationUnmarshaller;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * This resource allow access to the current tenant information. For complete tenant management API, see the
 * TenantManagerResource in the manager module.
 */
@Component(TenantResource.PATH)
@Path(TenantResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantResource extends AbstractAttachmentResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "tenant";

    @Inject
    private org.mayocat.context.WebContext context;

    @Inject
    private AccountsService accountsService;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private AddonsRepresentationUnmarshaller addonsRepresentationUnmarshaller;

    @Inject
    private Logger logger;

    @GET
    @Authorized
    @ExistingTenant
    public Response currentTenant()
    {
        TenantRepresentation representation =
                new TenantRepresentation(getGlobalTimeZone(), context.getTenant(), null, getImages());
        return Response.ok(representation).build();
    }

    @Path("images")
    @GET
    public List<ImageRepresentation> getImages()
    {
        List<ImageRepresentation> result = new ArrayList();
        Tenant tenant = context.getTenant();
        if (tenant == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }
        for (Attachment attachment : this.getAttachmentStore().findAllChildrenOf(tenant,
                Arrays.asList("png", "jpg", "jpeg", "gif")))
        {
            List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
            Image image = new Image(attachment, thumbnails);
            ImageRepresentation representation = new ImageRepresentation(image);
            if (tenant.getFeaturedImageId() != null) {
                if (tenant.getFeaturedImageId().equals(attachment.getId())) {
                    representation.setFeatured(true);
                }
            }

            result.add(representation);
        }

        return result;
    }

    @Path("attachments")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addAttachment(@PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title, @FormDataParam("description") String description,
            @QueryParam("featuredImage") @DefaultValue("false") Boolean featuredImage)
    {
        Tenant tenant = this.context.getTenant();
        if (tenant == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Attachment created = this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.of(tenant.getId()));

        if (isUploadOfFeaturedImage(tenant, fileDetail, featuredImage) && created != null) {

            // If this is an image and the product doesn't have a featured image yet, and the attachment was
            // successful, the we set this image as featured image.
            tenant.setFeaturedImageId(created.getId());

            try {
                this.accountsService.updateTenant(tenant);
            } catch (EntityDoesNotExistException | InvalidEntityException e) {
                // Fail silently. The attachment has been added successfully, that's what matter
                this.logger.warn("Failed to set first image as featured image for entity {} with id", tenant.getId());
            }
        }

        return Response.noContent().build();
    }

    private boolean isUploadOfFeaturedImage(Tenant tenant, FormDataContentDisposition fileDetail,
            boolean isFeaturedImage)
    {
        return isFeaturedImage ||
                tenant.getFeaturedImageId() == null && AttachmentUtils.isImage(fileDetail.getFileName());
    }

    @POST
    @Authorized
    @ExistingTenant
    public Response updateTenant(TenantRepresentation tenantRepresentation)
    {
        Tenant tenant = this.context.getTenant();

        tenant.setName(tenantRepresentation.getName());
        tenant.setContactEmail(tenantRepresentation.getContactEmail());
        tenant.setAddons(addonsRepresentationUnmarshaller.unmarshall(tenantRepresentation.getAddons()));

        try {
            this.accountsService.updateTenant(tenant);
            return Response.ok().build();
        } catch (InvalidEntityException e) {
            return Response.status(422).entity("Invalid entity").build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Authorized
    public Response updateTenant(Tenant updatedTenant)
    {
        try {
            if (context.getTenant() == null) {
                // Should not happen
                return Response.status(404).build();
            } else {
                updatedTenant.setSlug(context.getTenant().getSlug());
                this.accountsService.updateTenant(updatedTenant);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Tenant not found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    private DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue());
    }
}
