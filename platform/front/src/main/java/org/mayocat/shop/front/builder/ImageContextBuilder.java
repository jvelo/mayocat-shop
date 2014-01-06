/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.builder;

import java.text.MessageFormat;

import org.mayocat.configuration.images.ImageFormatDefinition;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.util.ImageUtils;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.context.ImageContext;
import org.mayocat.shop.front.util.ContextUtils;
import org.mayocat.theme.ThemeDefinition;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class ImageContextBuilder
{
    private ThemeDefinition theme;

    public ImageContextBuilder(ThemeDefinition theme)
    {
        this.theme = theme;
    }

    public ImageContext createImageContext(Image image)
    {
        return this.createImageContext(image, false);
    }

    public ImageContext createImageContext(Image image, boolean featured)
    {
        ImageContext context = new ImageContext("/attachments/" + image.getAttachment().getSlug() +
                "." + image.getAttachment().getExtension());

        context.setTitle(ContextUtils.safeString(image.getAttachment().getTitle()));
        context.setDescription(ContextUtils.safeString(image.getAttachment().getDescription()));
        context.put("featured", featured);
        context.put("url", MessageFormat.format("/images/{0}.{1}", image.getAttachment().getSlug(),
                image.getAttachment().getExtension()));

        if (theme != null && theme.getImageFormats().size() > 0) {
            for (String dimensionName : theme.getImageFormats().keySet()) {
                ImageFormatDefinition definition = theme.getImageFormats().get(dimensionName);
                Optional<Thumbnail> bestFit = findBestFit(image, definition.getWidth(),
                        definition.getHeight());

                if (bestFit.isPresent()) {
                    String url =
                            MessageFormat.format(
                                    "/images/thumbnails/{0}_{1,number,#}_{2,number,#}_{3,number,#}_{4,number,#}.{5}" +
                                            "?width={6,number,#}&height={7,number,#}",
                                    image.getAttachment().getSlug(),
                                    bestFit.get().getX(),
                                    bestFit.get().getY(),
                                    bestFit.get().getWidth(),
                                    bestFit.get().getHeight(),
                                    image.getAttachment().getExtension(),
                                    definition.getWidth(),
                                    definition.getHeight());
                    context.put("theme_" + dimensionName + "_url", url);
                } else {
                    String url =
                            MessageFormat.format("/images/{0}.{1}?width={2,number,#}&height={3,number,#}",
                                    image.getAttachment().getSlug(),
                                    image.getAttachment().getExtension(),
                                    definition.getWidth(),
                                    definition.getHeight());
                    context.put("theme_" + dimensionName + "_url", url);
                }
            }
        }
        return context;
    }

    public ImageContext createPlaceholderImageContext()
    {
        return createPlaceholderImageContext(false);
    }

    public ImageContext createPlaceholderImageContext(boolean featured)
    {
        ImageContext context = new ImageContext("http://placehold.it/800x800");
        if (theme != null && theme.getImageFormats().size() > 0) {
            for (String dimensionName : theme.getImageFormats().keySet()) {
                // Note: if only one dimension is passed for an image format (it means the image format is supposed
                // to respect the original image aspect ratio according to the one dimension passed), we present the
                // placeholder image as a square.
                ImageFormatDefinition definition = theme.getImageFormats().get(dimensionName);
                String url = MessageFormat.format("http://placehold.it/{0,number,#}x{1,number,#}",
                        definition.getWidth() != null ? definition.getWidth() : definition.getHeight(),
                        definition.getHeight() != null ? definition.getHeight() : definition.getWidth());
                context.put("theme_" + dimensionName + "_" + ContextConstants.URL, url);
            }
        }
        return context;
    }

    private Optional<Thumbnail> findBestFit(Image image, Integer width, Integer height)
    {
        if (width == null || height == null) {
            // First handle the case where we have only one dimension width or height
            for (Thumbnail thumbnail : image.getThumbnails()) {
                if ((thumbnail.getRatio().equals("1:0") && height == null) ||
                        (thumbnail.getRatio().equals("0:1") && width == null))
                {
                    return Optional.of(thumbnail);
                }
            }
            return Optional.absent();
        }

        // Then handle the general case where we have both dimensions width and height
        Thumbnail foundRatio = null;
        String expectedRatio = ImageUtils.imageRatio(width, height);

        for (Thumbnail thumbnail : image.getThumbnails()) {
            if (thumbnail.getRatio().equals(expectedRatio)) {
                if (thumbnail.getWidth().equals(width)
                        && thumbnail.getHeight().equals(height))
                {
                    // Exact match, stop searching
                    return Optional.of(thumbnail);
                } else {
                    // Ratio match, keep searching
                    foundRatio = thumbnail;
                }
            }
        }
        return Optional.fromNullable(foundRatio);
    }
}