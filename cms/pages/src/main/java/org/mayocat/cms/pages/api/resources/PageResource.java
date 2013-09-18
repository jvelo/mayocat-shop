package org.mayocat.cms.pages.api.resources;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.cms.pages.meta.PageEntity;
import org.mayocat.model.AddonFieldType;
import org.mayocat.model.AddonSource;
import org.mayocat.rest.Resource;
import org.mayocat.cms.pages.api.representations.PageRepresentation;
import org.mayocat.model.Addon;
import org.mayocat.rest.representations.ResultSetRepresentation;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.resources.AbstractAttachmentResource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.EntityReferenceRepresentation;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.yammer.metrics.annotation.Timed;

/**
 * @version $Id$
 */
@Component(PageResource.PATH)
@Path(PageResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class PageResource extends AbstractAttachmentResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + PageEntity.PATH;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Provider<PageStore> pageStore;

    @GET
    public Object listPages(@QueryParam("number") @DefaultValue("100") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        List<EntityReferenceRepresentation> pageReferences = Lists.newArrayList();
        List<Page> pages = pageStore.get().findAll(number, offset);

        for (Page page : pages) {
            pageReferences.add(new EntityReferenceRepresentation(PATH + "/" + page.getSlug(), page.getSlug(), page.getTitle()
            ));
        }

        Integer total = this.pageStore.get().countAll();
        ResultSetRepresentation<EntityReferenceRepresentation> resultSet =
                new ResultSetRepresentation<EntityReferenceRepresentation>(
                        PATH + "/",
                        offset, number,
                        pageReferences,
                        total
                );

        return resultSet;
    }

    @GET
    @Path("{slug}")
    public Object getPage(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Page page = pageStore.get().findBySlug(slug);
        if (page == null) {
            return Response.status(404).build();
        }

        List<String> expansions = Strings.isNullOrEmpty(expand)
                ? Collections.<String>emptyList()
                : Arrays.asList(expand.split(","));

        PageRepresentation representation;
        if (expansions.contains("images")) {
            List<ImageRepresentation> images = getImages(slug);
            representation = new PageRepresentation(page, images);
            if (images != null) {
                for (ImageRepresentation image : images) {
                    if (image.isFeaturedImage()) {
                        representation.setFeaturedImage(image);
                    }
                }
            }
        }
        else {
            representation= new PageRepresentation(page);
        }

        if (page.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : page.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            representation.setAddons(addons);
        }
        return representation;
    }

    @POST
    @Timed
    @Authorized //(roles = { Role.ADMIN })
    public Object createPage(Page page)
    {
        try {
            if (Strings.isNullOrEmpty(page.getSlug())) {
                page.setSlug(this.getSlugifier().slugify(page.getTitle()));
            }
            this.pageStore.get().create(page);

            Page created = pageStore.get().findBySlug(page.getSlug());

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/page/my-created-product
            return Response.created(new URI(created.getSlug())).build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A product with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @POST
    @Timed
    @Authorized
    // Partial update : NOT idempotent
    public Response updatePage(@PathParam("slug") String slug,
            PageRepresentation updatedPageRepresentation)
    {
        try {
            Page page = this.pageStore.get().findBySlug(slug);
            if (page == null) {
                return Response.status(404).build();
            } else {
                page.setTitle(updatedPageRepresentation.getTitle());
                page.setContent(updatedPageRepresentation.getContent());
                page.setModel(updatedPageRepresentation.getModel());
                page.setPublished(updatedPageRepresentation.getPublished());
                page.setLocalizedVersions(updatedPageRepresentation.getLocalizedVersions());

                // Addons
                if (updatedPageRepresentation.getAddons() != null) {
                    List<Addon> addons = Lists.newArrayList();
                    for (AddonRepresentation addonRepresentation : updatedPageRepresentation.getAddons()) {
                        Addon addon = new Addon();
                        addon.setSource(AddonSource.fromJson(addonRepresentation.getSource()));
                        addon.setType(AddonFieldType.fromJson(addonRepresentation.getType()));
                        addon.setValue(addonRepresentation.getValue());
                        addon.setKey(addonRepresentation.getKey());
                        addon.setGroup(addonRepresentation.getGroup());
                        addons.add(addon);
                    }
                    page.setAddons(addons);
                }

                // Featured image
                if (updatedPageRepresentation.getFeaturedImage() != null) {
                    ImageRepresentation representation = updatedPageRepresentation.getFeaturedImage();

                    Attachment featuredImage =
                            this.getAttachmentStore().findBySlugAndExtension(representation.getSlug(),
                                    representation.getFile().getExtension());
                    if (featuredImage != null) {
                        page.setFeaturedImageId(featuredImage.getId());
                    }
                }


                this.pageStore.get().update(page);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No page with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @DELETE
    @Authorized
    @Consumes(MediaType.WILDCARD)
    public Response deletePage(@PathParam("slug") String slug)
    {
        Page page = this.pageStore.get().findBySlug(slug);

        if (page == null) {
            return Response.status(404).build();
        }

        try {
            this.pageStore.get().delete(page);

            return Response.noContent().build();

        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No page with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}/images")
    @GET
    public List<ImageRepresentation> getImages(@PathParam("slug") String slug)
    {
        List<ImageRepresentation> result = new ArrayList();
        Page page = this.pageStore.get().findBySlug(slug);
        if (page == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        for (Attachment attachment : this.getAttachmentStore().findAllChildrenOf(page,
                Arrays.asList("png", "jpg", "jpeg", "gif")))
        {
            List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
            Image image = new Image(attachment, thumbnails);
            ImageRepresentation representation = new ImageRepresentation(image);

            if (page.getFeaturedImageId() != null) {
                if (page.getFeaturedImageId().equals(attachment.getId())) {
                    representation.setFeatured(true);
                }
            }

            result.add(representation);
        }

        return result;
    }

    @Path("{slug}/images/{imageSlug}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    public Response detachImage(@PathParam("slug") String slug, @PathParam("imageSlug") String imageSlug)
    {
        Attachment attachment = getAttachmentStore().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            getAttachmentStore().detach(attachment);
            return Response.noContent().build();

        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        }
    }

    @Path("{slug}/attachments")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addAttachment(@PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title, @FormDataParam("description") String description)
    {
        Page page = this.pageStore.get().findBySlug(slug);
        if (page == null) {
            return Response.status(404).build();
        }

        this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description, Optional.of(page.getId()));
        return Response.noContent().build();
    }
}
