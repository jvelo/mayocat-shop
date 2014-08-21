package org.mayocat.rest.resources;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.mayocat.attachment.model.LoadedAttachment;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.image.ImageService;
import org.mayocat.rest.parameters.ImageOptions;
import org.slf4j.Logger;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AbstractImageResource
{
    @Inject
    private ImageService imageService;

    @Inject
    private Logger logger;

    @Inject
    protected Provider<AttachmentStore> attachmentStore;

    private static final List<String> IMAGE_EXTENSIONS = new ArrayList<String>();

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("gif");
        IMAGE_EXTENSIONS.add("png");
    }

    public Response downloadThumbnail(@PathParam("slug") String slug, @PathParam("ext") String extension,
            @PathParam("x") Integer x, @PathParam("y") Integer y, @PathParam("width") Integer width,
            @PathParam("height") Integer height, @Context ServletContext servletContext,
            @Context Optional<ImageOptions> imageOptions)
    {
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            // Refuse to treat a request with image options for a non-image attachment
            return Response.status(Response.Status.BAD_REQUEST).entity("Not an image").build();
        }

        String fileName = slug + "." + extension;
        LoadedAttachment file = this.attachmentStore.get().findAndLoadBySlugAndExtension(slug, extension);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Rectangle boundaries = new Rectangle(x, y, width, height);

            if (imageOptions.isPresent()) {
                Optional<Dimension> newDimension = imageService.newDimension(boundaries,
                        imageOptions.get().getWidth(),
                        imageOptions.get().getHeight());

                Dimension dimensions = newDimension.or(
                        new Dimension(imageOptions.get().getWidth().or(-1), imageOptions.get().getHeight().or(-1)));

                return Response.ok(imageService.getImage(file, dimensions, boundaries),
                        servletContext.getMimeType(fileName))
                        .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                        .build();
            } else {
                return Response.ok(imageService.getImage(file, boundaries),
                        servletContext.getMimeType(fileName))
                        .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                        .build();
            }
        } catch (IOException e) {
            this.logger.warn("Failed to scale image for attachment [{slug}]", slug);
            return Response.serverError().entity("Failed to scale image").build();
        }
    }

    public Response downloadImage(String slug, String extension, ServletContext servletContext,
            Optional<ImageOptions> imageOptions)
    {
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            // Refuse to treat a request with image options for a non-image attachment
            return Response.status(Response.Status.BAD_REQUEST).entity("Not an image").build();
        }

        if (imageOptions.isPresent()) {

            String fileName = slug + "." + extension;
            LoadedAttachment file = this.attachmentStore.get().findAndLoadBySlugAndExtension(slug, extension);
            if (file == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            try {
                if (imageOptions.get().getHeight().isPresent() && imageOptions.get().getWidth().isPresent()) {
                    // Both width and height set -> calculate a fitting box

                    Dimension dimension =
                            new Dimension(imageOptions.get().getWidth().get(), imageOptions.get().getHeight().get());
                    Optional<Rectangle> fittingBox = imageService.getFittingRectangle(file, dimension);

                    InputStream image;
                    if (fittingBox.isPresent()) {
                        image = imageService.getImage(file, dimension, fittingBox.get());
                    } else {
                        image = imageService.getImage(file, dimension);
                    }
                    return Response.ok(image, servletContext.getMimeType(fileName))
                            .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                            .build();
                } else {

                    Optional<Dimension> newDimension = imageService.newDimension(file,
                            imageOptions.get().getWidth(),
                            imageOptions.get().getHeight());

                    if (newDimension.isPresent()) {
                        return Response.ok(imageService.getImage(file, newDimension.get()),
                                servletContext.getMimeType(fileName))
                                .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                                .build();
                    }

                    // data stream has been consumed, load it again
                    file = this.attachmentStore.get().findAndLoadBySlugAndExtension(slug, extension);

                    return Response.ok(file.getData().getStream(), servletContext.getMimeType(fileName))
                            .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                            .build();
                }
            } catch (IOException e) {
                this.logger.warn("Failed to scale image for attachment [{slug}]", slug);
                return Response.serverError().entity("Failed to scale image").build();
            }
        }

        return this.downloadFile(slug, extension, servletContext);
    }

    public Response downloadFile(String slug, String extension, ServletContext servletContext)
    {
        String fileName = slug + "." + extension;
        LoadedAttachment file = this.attachmentStore.get().findAndLoadBySlugAndExtension(slug, extension);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(file.getData().getStream(), servletContext.getMimeType(fileName))
                .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                .build();
    }

}
