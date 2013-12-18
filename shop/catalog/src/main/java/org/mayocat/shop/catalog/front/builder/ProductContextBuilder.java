/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mayocat.addons.front.builder.AddonContextBuilder;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.catalog.front.representation.PriceRepresentation;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.util.ContextUtils;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.url.EntityURLFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ProductContextBuilder implements ContextConstants
{
    private CatalogSettings catalogSettings;

    private GeneralSettings generalSettings;

    private ThemeDefinition theme;

    private ImageContextBuilder imageContextBuilder;

    private AddonContextBuilder addonContextBuilder;

    private CollectionContextBuilder collectionContextBuilder;

    private EntityLocalizationService entityLocalizationService;

    private EntityURLFactory urlFactory;

    public ProductContextBuilder(EntityURLFactory urlFactory, ConfigurationService configurationService,
            EntityLocalizationService entityLocalizationService, ThemeDefinition theme)
    {
        this.urlFactory = urlFactory;

        catalogSettings = configurationService.getSettings(CatalogSettings.class);
        generalSettings = configurationService.getSettings(GeneralSettings.class);

        this.entityLocalizationService = entityLocalizationService;

        this.theme = theme;

        imageContextBuilder = new ImageContextBuilder(theme);
        addonContextBuilder = new AddonContextBuilder();
        collectionContextBuilder = new CollectionContextBuilder(urlFactory, theme);
    }

    public Map<String, Object> build(final Product product, List<Image> images)
    {
        Map<String, Object> productContext = Maps.newHashMap();

        productContext.put("title", ContextUtils.safeString(product.getTitle()));
        productContext.put("description", ContextUtils.safeHtml(product.getDescription()));
        productContext.put(URL, urlFactory.create(product).getPath());
        productContext.put(SLUG, product.getSlug());

        // Prices
        if (product.getUnitPrice() != null) {
            final Locale locale = generalSettings.getLocales().getMainLocale().getValue();
            final Currency currency = catalogSettings.getCurrencies().getMainCurrency().getValue();
            productContext.put("unitPrice", new PriceRepresentation(product.getUnitPrice(), currency, locale));
        }

        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();

        Image featuredImage = null;

        if (images.size() > 0) {
            for (Image image : images) {
                if (featuredImage == null && image.getAttachment().getId().equals(product.getFeaturedImageId())) {
                    featuredImage = image;
                }
                allImages.add(imageContextBuilder.createImageContext(image, image == featuredImage));
            }
            if (featuredImage == null) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = images.get(0);
            }
            imagesContext.put("featured", imageContextBuilder.createImageContext(featuredImage, true));
        } else {
            // Create placeholder image
            Map<String, String> placeholder = imageContextBuilder.createPlaceholderImageContext(true);
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        productContext.put("images", imagesContext);

        // Addons

        if (product.getAddons().isLoaded()) {
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            productContext.put("theme_addons", addonContextBuilder.build(themeAddons, product.getAddons().get()));
        }

        // Collection

        if (product.getFeaturedCollection().isLoaded()) {
            Map<String, Object> featuredCollection = collectionContextBuilder.build(
                    entityLocalizationService.localize(product.getFeaturedCollection().get()),
                    Collections.<Image>emptyList());
            productContext.put("featured_collection", featuredCollection);
        }

        return productContext;
    }
}
