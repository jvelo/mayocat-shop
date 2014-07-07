/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.cms.pages.model.Page
import org.mayocat.image.model.Image
import org.mayocat.rest.web.object.EntityImagesWebObject
import org.mayocat.rest.web.object.EntityModelWebObject
import org.mayocat.rest.web.object.ImageWebObject
import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory

/**
 * Web object for a {@link Page}
 *
 * @version $Id$
 */
@CompileStatic
class PageWebObject {

    String title

    String content

    String url

    String slug

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityModelWebObject model

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String template

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityImagesWebObject images

    /**
     * @deprecated use #addons
     */
    @Deprecated
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> theme_addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map <String, Object> addons


    def withPage(Page page, EntityURLFactory urlFactory, ThemeFileResolver themeFileResolver)
    {
        title = ContextUtils.safeString(page.title)
        content = ContextUtils.safeHtml(page.content)
        url = urlFactory.create(page).path
        slug = page.slug
        if (page.model.isPresent() && themeFileResolver.resolveModelPath(page.model.get()).isPresent()) {
            model = new EntityModelWebObject([
                    template: themeFileResolver.resolveModelPath(page.model.get()).get(),
                    slug: page.model.get()
            ])
            template = themeFileResolver.resolveModelPath(page.model.get()).get();
        } else {
            template = "page.html";
        }
    }

    def withAddons(Map<String, Object> addons) {
        theme_addons = addons
        this.addons = addons
    }

    def withImages(List<Image> imagesList, UUID featuredImageId, Optional<ThemeDefinition> theme)
    {
        List<ImageWebObject> all = [];
        ImageWebObject featuredImage;

        if (imagesList.size() > 0) {
            for (Image image : imagesList) {
                def featured = image.attachment.id.equals(featuredImageId)
                ImageWebObject imageWebObject = new ImageWebObject();
                imageWebObject.withImage(image, featured, theme)
                if (featuredImage == null && featured) {
                    featuredImage = imageWebObject;
                }
                all << imageWebObject
            }
            if (featuredImage == null) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = all.get(0)
            }
        } else {
            // Create placeholder image
            featuredImage = new ImageWebObject()
            featuredImage.withPlaceholderImage(true, theme)
            all = [ featuredImage ]
        }
        images = new EntityImagesWebObject([
                all: all,
                featured: featuredImage
        ])
    }
}
