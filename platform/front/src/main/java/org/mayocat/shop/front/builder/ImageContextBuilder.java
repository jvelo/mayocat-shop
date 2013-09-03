package org.mayocat.shop.front.builder;

import java.text.MessageFormat;
import java.util.Map;

import org.mayocat.configuration.thumbnails.ThumbnailDefinition;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.util.ImageUtils;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.context.ImageContext;
import org.mayocat.shop.front.util.ContextUtils;
import org.mayocat.theme.Theme;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ImageContextBuilder
{
    private Theme theme;

    public ImageContextBuilder(Theme theme)
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

        if (theme != null && theme.getThumbnails().size() > 0) {
            for (String dimensionName : theme.getThumbnails().keySet()) {
                ThumbnailDefinition definition = theme.getThumbnails().get(dimensionName);
                Optional<Thumbnail> bestFit = findBestFit(image, definition.getWidth(),
                        definition.getHeight());

                if (bestFit.isPresent()) {
                    String url =
                            MessageFormat.format("/images/thumbnails/{0}_{1}_{2}_{3}_{4}.{5}?width={6}&height={7}",
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
                            MessageFormat.format("/images/{0}.{1}?width={2}&height={3}",
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
        if (theme != null && theme.getThumbnails().size() > 0) {
            for (String dimensionName : theme.getThumbnails().keySet()) {

                ThumbnailDefinition definition = theme.getThumbnails().get(dimensionName);
                String url = MessageFormat.format("http://placehold.it/{0}x{1}", definition.getWidth(),
                        definition.getHeight());
                context.put("theme_" + dimensionName + "_" + ContextConstants.URL, url);
            }
        }
        return context;
    }

    private Optional<Thumbnail> findBestFit(Image image, Integer width, Integer height)
    {
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