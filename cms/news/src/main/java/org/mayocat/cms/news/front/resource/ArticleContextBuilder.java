/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.front.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.front.builder.AddonContextBuilder;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.cms.news.model.Article;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.context.DateContext;
import org.mayocat.shop.front.util.ContextUtils;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.url.EntityURLFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ArticleContextBuilder
{
    private ThemeDefinition theme;

    private ConfigurationService configurationService;

    private EntityURLFactory urlFactory;

    public ArticleContextBuilder(ThemeDefinition themeDefinition, ConfigurationService configurationService,
            EntityURLFactory urlFactory)
    {
        this.theme = themeDefinition;
        this.configurationService = configurationService;
        this.urlFactory = urlFactory;
    }

    public Map<String, Object> build(Article article, List<Image> images)
    {

        GeneralSettings settings;
        settings = configurationService.getSettings(GeneralSettings.class);

        Map<String, Object> context = Maps.newHashMap();
        context.put("title", ContextUtils.safeString(article.getTitle()));
        context.put("content", ContextUtils.safeHtml(article.getContent()));
        context.put(ContextConstants.URL, urlFactory.create(article).getPath());
        context.put(ContextConstants.SLUG, article.getSlug());

        if (article.getPublicationDate() != null) {
            DateContext date =
                    new DateContext(article.getPublicationDate(),
                            settings.getLocales().getMainLocale().getValue());
            context.put("publicationDate", date);
        }

        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();
        ImageContextBuilder imageContextBuilder = new ImageContextBuilder(theme);
        Image featuredImage = null;

        if (images.size() > 0) {
            for (Image image : images) {
                if (featuredImage == null && image.getAttachment().getId().equals(article.getFeaturedImageId())) {
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

        // Addons
        if (article.getAddons().isLoaded() && theme != null) {
            AddonContextBuilder addonContextBuilder = new AddonContextBuilder();
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            context.put("theme_addons", addonContextBuilder.build(themeAddons, article.getAddons().get()));
        }

        imagesContext.put("all", allImages);
        context.put("images", imagesContext);

        return context;
    }
}