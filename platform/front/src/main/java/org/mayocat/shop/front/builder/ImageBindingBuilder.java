package org.mayocat.shop.front.builder;

import java.text.MessageFormat;
import java.util.Map;

import org.mayocat.configuration.thumbnails.Dimensions;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.util.ImageUtils;
import org.mayocat.theme.Theme;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ImageBindingBuilder
{
    private Theme theme;

    public ImageBindingBuilder(Theme theme)
    {
        this.theme = theme;
    }

    public Map<String, String> createImageContext(Image image)
    {
        Map<String, String> context = Maps.newHashMap();

        context.put("url", "/attachment/" + image.getAttachment().getSlug() +
                "." + image.getAttachment().getExtension());

        for (String dimensionName : theme.getThumbnails().keySet()) {
            // TODO validate dimension name
            Dimensions dimensions = theme.getThumbnails().get(dimensionName);
            Optional<Thumbnail> bestFit = findBestFit(image, dimensions);

            if (bestFit.isPresent()) {
                String url =
                        MessageFormat.format("/image/thumbnails/{0}_{1}_{2}_{3}_{4}.{5}?width={6}&height={7}",
                                image.getAttachment().getSlug(),
                                bestFit.get().getX(),
                                bestFit.get().getY(),
                                bestFit.get().getWidth(),
                                bestFit.get().getHeight(),
                                image.getAttachment().getExtension(),
                                dimensions.getWidth(),
                                dimensions.getHeight());
                context.put("theme_" + dimensionName + "_url", url);
            } else {
                String url =
                        MessageFormat.format("/image/{0}.{1}?width={2}&height={3}",
                                image.getAttachment().getSlug(),
                                image.getAttachment().getExtension(),
                                dimensions.getWidth(),
                                dimensions.getHeight());
                context.put("theme_" + dimensionName + "_url", url);
            }
        }
        return context;
    }

    public Map<String, String> createPlaceholderImageContext()
    {
        Map<String, String> context = Maps.newHashMap();
        context.put("url", "http://placehold.it/800x800");
        for (String dimensionName : theme.getThumbnails().keySet()) {

            Dimensions dimensions = theme.getThumbnails().get(dimensionName);
            String url = MessageFormat.format("http://placehold.it/{0}x{1}", dimensions.getWidth(),
                    dimensions.getHeight());
            context.put("theme_" + dimensionName + "_url", url);
        }
        return context;
    }

    private Optional<Thumbnail> findBestFit(Image image, Dimensions dimensions)
    {
        Thumbnail foundRatio = null;
        String expectedRatio = ImageUtils.imageRatio(dimensions.getWidth(), dimensions.getHeight());

        for (Thumbnail thumbnail : image.getThumbnails()) {
            if (thumbnail.getRatio().equals(expectedRatio)) {
                if (thumbnail.getWidth().equals(dimensions.getWidth())
                        && thumbnail.getHeight().equals(dimensions.getHeight()))
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