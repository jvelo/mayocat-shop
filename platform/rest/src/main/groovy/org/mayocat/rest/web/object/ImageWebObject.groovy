/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.web.object

import com.google.common.base.Optional
import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import org.mayocat.configuration.images.ImageFormatDefinition
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.util.ImageUtils
import org.mayocat.rest.util.RestUtils
import org.mayocat.theme.ThemeDefinition

import java.text.MessageFormat

/**
 * Web object for an {@link Image} representation
 *
 * @version $Id$
 */
@CompileStatic
class ImageWebObject extends HashMap<String, Object>
{
    def withImage(Image image, boolean isFeatured, Optional<ThemeDefinition> theme)
    {
        put "title", RestUtils.safeString(image.attachment.title) as String;
        put "description", RestUtils.safeString(image.attachment.description) as String;
        put "featured", isFeatured

        def templateEngine = new SimpleTemplateEngine()

        put "url", templateEngine.createTemplate('/images/${slug}.${extension}').make([
                slug: image.attachment.slug,
                extension: image.attachment.extension
        ]).toString();

        if (theme.isPresent() && theme.get().imageFormats.size() > 0) {
            for (String dimensionName : theme.get().imageFormats.keySet()) {
                def definition = theme.get().imageFormats.get(dimensionName);
                Optional<Thumbnail> bestFit = findBestFit(image, definition.width, definition.height);

                if (bestFit.isPresent()) {
                    String url = MessageFormat.format(
                            "/images/thumbnails/{0}_{1,number,#}_{2,number,#}_{3,number,#}_{4,number,#}.{5}" +
                                    "?width={6,number,#}&height={7,number,#}",
                            image.attachment.slug,
                            bestFit.get().x,
                            bestFit.get().y,
                            bestFit.get().width,
                            bestFit.get().height,
                            image.attachment.extension,
                            definition.width,
                            definition.height
                    );
                    put "theme_${dimensionName}_url" as String, url as String
                } else {
                    String url = MessageFormat.format("/images/{0}.{1}?width={2,number,#}&height={3,number,#}",
                            image.attachment.slug,
                            image.attachment.extension,
                            definition.width,
                            definition.height
                    );
                    put "theme_${dimensionName}_url" as String, url as String
                }
            }
        }
    }

    def withPlaceholderImage(Optional<ThemeDefinition> theme)
    {
        withPlaceholderImage(false, theme);
    }

    def withPlaceholderImage(boolean featured, Optional<ThemeDefinition> theme)
    {
        if (theme.isPresent() && theme.get().imageFormats.size() > 0) {
            for (String dimensionName : theme.get().imageFormats.keySet()) {
                // Note: if only one dimension is passed for an image format (it means the image format is supposed
                // to respect the original image aspect ratio according to the one dimension passed), we present the
                // placeholder image as a square.
                ImageFormatDefinition definition = theme.get().imageFormats.get(dimensionName);
                String url = MessageFormat.format("http://placehold.it/{0,number,#}x{1,number,#}",
                        definition.width != null ? definition.width : definition.height,
                        definition.height != null ? definition.height : definition.width);

                put "theme_${dimensionName}_url" as String, url as String
            }
        }
    }

    def Optional<Thumbnail> findBestFit(Image image, Integer width, Integer height)
    {
        if (width == null || height == null) {
            // First handle the case where we have only one dimension width or height
            for (Thumbnail thumbnail : image.thumbnails) {
                if ((thumbnail.ratio.equals("1:0") && height == null) ||
                        (thumbnail.ratio.equals("0:1") && width == null))
                {
                    return Optional.of(thumbnail);
                }
            }
            return Optional.absent();
        }

        // Then handle the general case where we have both dimensions width and height
        Thumbnail foundRatio = null;
        String expectedRatio = ImageUtils.imageRatio(width, height);

        for (Thumbnail thumbnail : image.thumbnails) {
            if (thumbnail.ratio.equals(expectedRatio)) {
                if (thumbnail.width.equals(width)
                        && thumbnail.height.equals(height))
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
