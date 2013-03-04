package org.mayocat.shop.front.builder;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mayocat.configuration.general.GeneralConfiguration;
import org.mayocat.configuration.thumbnails.Dimensions;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.util.ImageUtils;
import org.mayocat.shop.catalog.configuration.shop.CatalogConfiguration;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.theme.Theme;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ProductBindingBuilder
{
    private CatalogConfiguration catalogConfiguration;

    private GeneralConfiguration generalConfiguration;

    private Theme theme;

    public ProductBindingBuilder(CatalogConfiguration catalogConfiguration, GeneralConfiguration generalConfiguration,
            Theme theme)
    {
        this.catalogConfiguration = catalogConfiguration;
        this.generalConfiguration = generalConfiguration;
        this.theme = theme;
    }

    public Map<String, Object> build(final Product product, List<Image> images)
    {
        Map<String, Object> productContext = Maps.newHashMap();

        productContext.put("title", product.getTitle());
        productContext.put("description", product.getDescription());

        // Prices
        if (product.getPrice() != null) {
            final Locale locale = generalConfiguration.getLocales().getMainLocale().getValue();
            final Currency currency = catalogConfiguration.getCurrencies().getMainCurrency().getValue();
            productContext.put("price", new HashMap<String, Object>()
            {
                {
                    put("amount", product.getPrice());
                    put("currency", new HashMap<String, Object>()
                    {
                        {
                            put("code", currency.getCurrencyCode());
                            put("symbol", currency.getSymbol(locale));
                        }
                    });
                }
            });

            // TODO
            // - distinguish between two symbols : "absolute" and "internationalized" (i.e. "$" vs. "US$")
            // - look into amount formatting.
            // Check http://joda-money.sourceforge.net/apidocs/org/joda/money/format/MoneyFormatter.html
            // - handle multiple prices (unit, discounts, etc.)
        }

        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();

        if (images.size() > 0) {
            imagesContext.put("featured", createImageContext(images.get(0)));
            for (Image image : images) {
                allImages.add(createImageContext(image));
            }
        } else {
            Map<String, String> placeholder = createPlaceholderImageContext();
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        productContext.put("images", imagesContext);

        return productContext;
    }

    private Map<String, String> createImageContext(Image image)
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
                                bestFit.get().getX(),
                                bestFit.get().getY(),
                                bestFit.get().getWidth(),
                                bestFit.get().getHeight(),
                                image.getAttachment().getSlug(),
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

    private Map<String, String> createPlaceholderImageContext()
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
