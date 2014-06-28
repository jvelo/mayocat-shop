/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.addons.front.builder.AddonContextBuilder
import org.mayocat.addons.model.AddonGroupDefinition
import org.mayocat.cms.news.model.Article
import org.mayocat.image.model.Image
import org.mayocat.rest.api.object.DateWebObject
import org.mayocat.rest.web.object.EntityImagesWebObject
import org.mayocat.rest.web.object.EntityModelWebObject
import org.mayocat.rest.web.object.ImageWebObject
import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.theme.ThemeDefinition
import org.mayocat.url.EntityURLFactory

/**
 * Web object for an {@link Article}
 *
 * @version $Id$
 */
@CompileStatic
class ArticleWebObject {

    String title

    String content

    String url

    String slug

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityModelWebObject model

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityImagesWebObject images

    DateWebObject publicationDate

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map theme_addons

    def withArticle(Article article, EntityURLFactory urlFactory, Locale locale,  Optional<ThemeDefinition> theme)
    {
        title = ContextUtils.safeString(article.title)
        content = ContextUtils.safeHtml(article.content)
        url = urlFactory.create(article).path
        slug = article.slug

        publicationDate = new DateWebObject()
        publicationDate.withDate(article.publicationDate, locale)

        // Addons
        if (article.addons.isLoaded() && theme.isPresent()) {
            def addonContextBuilder = new AddonContextBuilder();
            Map<String, AddonGroupDefinition> themeAddons = theme.get().addons
            theme_addons = addonContextBuilder.build(themeAddons, article.addons.get());
        }
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
