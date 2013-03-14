package org.mayocat.cms.pages.api.resources;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.Slugifier;
import org.mayocat.accounts.model.Role;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.base.Resource;
import org.mayocat.cms.pages.api.representations.PageRepresentation;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.rest.AbstractAttachmentResource;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.representations.ImageRepresentation;
import org.mayocat.store.EntityAlreadyExistsException;
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
@Component("/api/1.0/page/")
@Path("/api/1.0/page/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class PageResource extends AbstractAttachmentResource implements Resource
{
    @Inject
    private Slugifier slugifier;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Provider<PageStore> pageStore;

    @GET
    public Object listPages()
    {
        List<PageRepresentation> result = Lists.newArrayList();
        List<Page> pages = pageStore.get().findAll(0, 0);

        for (Page page : pages) {
            result.add(new PageRepresentation(page));
        }

        return result;
    }

    @GET
    @Path("{slug}")
    public Object getPage(@PathParam("slug") String slug)
    {
        Page page = pageStore.get().findBySlug(slug);
        if (page == null) {
            return Response.status(404).build();
        }
        return new PageRepresentation(page);
    }

    @POST
    @Timed
    @Authorized(roles = { Role.ADMIN })
    public Object createPage(Page page)
    {
        try {
            if (Strings.isNullOrEmpty(page.getSlug())) {
                page.setSlug(this.slugifier.slugify(page.getTitle()));
            }
            this.pageStore.get().create(page);

            Page created = pageStore.get().findBySlug(page.getSlug());

            return Response.seeOther(new URI("/api/1.0/page/" + created.getSlug())).build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A product with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}/image")
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

            result.add(representation);
        }

        return result;
    }

    @Path("{slug}/attachment")
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

        return this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.of(page.getId()));
    }
}
