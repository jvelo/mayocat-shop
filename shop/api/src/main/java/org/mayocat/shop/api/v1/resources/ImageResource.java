package org.mayocat.shop.api.v1.resources;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.mayocat.base.Resource;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.image.util.ImageUtils;
import org.mayocat.model.Attachment;
import org.mayocat.rest.representations.ThumbnailRepresentation;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.store.AttachmentStore;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/api/1.0/image")
@Path("/api/1.0/image")
@ExistingTenant
public class ImageResource implements Resource
{
    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @PUT
    @Path("/{slug}/thumbnail/")
    public Response createThumbnail(@PathParam("slug") String slug, @Valid
    ThumbnailRepresentation thumbnailRepresentation)
    {
        Attachment file = this.attachmentStore.get().findBySlug(slug);

        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setAttachmentId(file.getId());
        thumbnail.setSource(thumbnailRepresentation.getSource());
        thumbnail.setHint(thumbnailRepresentation.getHint());
        thumbnail.setX(thumbnailRepresentation.getX());
        thumbnail.setY(thumbnailRepresentation.getY());
        thumbnail.setWidth(thumbnailRepresentation.getWidth());
        thumbnail.setHeight(thumbnailRepresentation.getHeight());

        thumbnail.setRatio(ImageUtils.imageRatio(thumbnail.getWidth(), thumbnail.getHeight()));

        this.thumbnailStore.get().createOrUpdateThumbnail(thumbnail);

        return Response.ok().build();
    }
}
