/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.resources;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
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

import org.mayocat.rest.Resource;
import org.mayocat.image.ImageService;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.parameters.ImageOptions;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Component("/images/")
@Path("/images/")
public class ImageResource extends AbstractAttachmentResource implements Resource
{
    @Inject
    private ImageService imageService;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Logger logger;

    private static final List<String> IMAGE_EXTENSIONS = new ArrayList<String>();

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("gif");
        IMAGE_EXTENSIONS.add("png");
    }

    @GET
    @Path("thumbnails/{slug}_{x: \\d+}_{y: \\d+}_{width: \\d+}_{height: \\d+}.{ext}")
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
        Attachment file = this.getAttachmentStore().findBySlugAndExtension(slug, extension);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Image image = imageService.readImage(file.getData());
            Rectangle boundaries = new Rectangle(x, y, width, height);
            RenderedImage cropped = imageService.cropImage(image, boundaries);

            if (imageOptions.isPresent()) {
                Optional<Dimension> newDimension = imageService.newDimension((Image) cropped,
                        imageOptions.get().getWidth(),
                        imageOptions.get().getHeight());

                Dimension dimensions = newDimension.or(
                        new Dimension(imageOptions.get().getWidth().get(), imageOptions.get().getHeight().get()));

                return Response.ok(imageService.scaleImage((Image) cropped, dimensions),
                        servletContext.getMimeType(fileName))
                        .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                        .build();
            } else {
                return Response.ok(cropped, servletContext.getMimeType(fileName))
                        .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                        .build();
            }
        } catch (IOException e) {
            this.logger.warn("Failed to scale image for attachment [{slug}]", slug);
            return Response.serverError().entity("Failed to scale image").build();
        }
    }

    @GET
    @Path("{slug}.{ext}")
    public Response downloadImage(@PathParam("slug") String slug, @PathParam("ext") String extension,
            @Context ServletContext servletContext, @Context Optional<ImageOptions> imageOptions)
    {
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            // Refuse to treat a request with image options for a non-image attachment
            return Response.status(Response.Status.BAD_REQUEST).entity("Not an image").build();
        }

        if (imageOptions.isPresent()) {

            String fileName = slug + "." + extension;
            Attachment file = this.getAttachmentStore().findBySlugAndExtension(slug, extension);
            if (file == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            try {
                Image image = imageService.readImage(file.getData());

                if (imageOptions.get().getHeight().isPresent() && imageOptions.get().getWidth().isPresent()) {
                    // Both width and height set -> calculate a fitting box

                    Dimension dimension =
                            new Dimension(imageOptions.get().getWidth().get(), imageOptions.get().getHeight().get());
                    Optional<Rectangle> fittingBox = imageService.getFittingRectangle(image, dimension);

                    RenderedImage scaled;

                    if (fittingBox.isPresent()) {
                        RenderedImage cropped = imageService.cropImage(image, fittingBox.get());
                        scaled = imageService.scaleImage((Image) cropped, dimension);
                    } else {
                        scaled = imageService.scaleImage(image, dimension);
                    }

                    return Response.ok(scaled, servletContext.getMimeType(fileName))
                            .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                            .build();
                } else {
                    Optional<Dimension> newDimension = imageService.newDimension(image,
                            imageOptions.get().getWidth(),
                            imageOptions.get().getHeight());

                    if (newDimension.isPresent()) {
                        return Response.ok(imageService.scaleImage(image, newDimension.get()),
                                servletContext.getMimeType(fileName))
                                .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                                .build();
                    }

                    return Response.ok(image, servletContext.getMimeType(fileName))
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
}
