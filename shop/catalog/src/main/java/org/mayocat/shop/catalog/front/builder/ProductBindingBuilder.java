package org.mayocat.shop.catalog.front.builder;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.builder.AddonBindingBuilderHelper;
import org.mayocat.shop.front.builder.ImageBindingBuilder;
import org.mayocat.theme.Theme;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ProductBindingBuilder
{
    private CatalogSettings catalogSettings;

    private GeneralSettings generalSettings;

    private Theme theme;

    private ImageBindingBuilder imageBindingBuilder;

    public ProductBindingBuilder(CatalogSettings catalogSettings, GeneralSettings generalSettings,
            Theme theme)
    {
        this.catalogSettings = catalogSettings;
        this.generalSettings = generalSettings;
        this.theme = theme;

        imageBindingBuilder = new ImageBindingBuilder(theme);
    }

    public Map<String, Object> build(final Product product, List<Image> images)
    {
        Map<String, Object> productContext = Maps.newHashMap();

        productContext.put("title", product.getTitle());
        productContext.put("description", product.getDescription());
        productContext.put("href", "/product/" + product.getSlug());

        // Prices
        if (product.getPrice() != null) {
            final Locale locale = generalSettings.getLocales().getMainLocale().getValue();
            final Currency currency = catalogSettings.getCurrencies().getMainCurrency().getValue();
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
            imagesContext.put("featured", imageBindingBuilder.createImageContext(images.get(0)));
            for (Image image : images) {
                allImages.add(imageBindingBuilder.createImageContext(image));
            }
        } else {
            Map<String, String> placeholder = imageBindingBuilder.createPlaceholderImageContext();
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        productContext.put("images", imagesContext);

        // Addons

        if (product.getAddons().isLoaded()) {
            Map<String, Object> themeAddonsContext = Maps.newHashMap();
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            for (String groupKey : themeAddons.keySet()) {

                AddonGroup group = themeAddons.get(groupKey);
                Map<String, Object> groupContext = Maps.newHashMap();

                for (String field : group.getFields().keySet()) {
                    Optional<org.mayocat.model.Addon> addon =
                            AddonBindingBuilderHelper.findAddon(groupKey, field, product.getAddons().get());
                    if (addon.isPresent()) {
                        groupContext.put(field, addon.get().getValue());
                    } else {
                        groupContext.put(field, null);
                    }
                }

                themeAddonsContext.put(groupKey, groupContext);
            }
            productContext.put("theme_addons", themeAddonsContext);
        }

        return productContext;
    }

}
