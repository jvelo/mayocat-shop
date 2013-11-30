/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.front.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.front.builder.AddonContextBuilder;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.image.model.Image;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.url.EntityURLFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PageContextBuilder
{
    private ThemeDefinition theme;

    private ImageContextBuilder imageContextBuilder;

    private AddonContextBuilder addonContextBuilder;

    private EntityURLFactory urlFactory;

    public PageContextBuilder(EntityURLFactory urlFactory, ThemeDefinition theme)
    {
        this.urlFactory = urlFactory;
        this.theme = theme;

        imageContextBuilder = new ImageContextBuilder(theme);
        addonContextBuilder = new AddonContextBuilder();
    }

    public Map<String, Object> build(final Page page, List<Image> images)
    {
        Map<String, Object> pageContext = Maps.newHashMap();

        pageContext.put("title", page.getTitle());
        pageContext.put("content", page.getContent());
        pageContext.put("published", page.getPublished());
        pageContext.put(ContextConstants.URL, urlFactory.create(page));
        pageContext.put(ContextConstants.SLUG, page.getSlug());

        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();

        if (images.size() > 0) {
            Image featuredImage = null;
            for (Image image : images) {
                if (featuredImage == null && image.getAttachment().getId().equals(page.getFeaturedImageId())) {
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
            Map<String, String> placeholder = imageContextBuilder.createPlaceholderImageContext();
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        pageContext.put("images", imagesContext);

        // Addons
        if (page.getAddons().isLoaded()) {
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            pageContext.put("theme_addons", addonContextBuilder.build(themeAddons, page.getAddons().get()));
        }

        return pageContext;
    }
}
