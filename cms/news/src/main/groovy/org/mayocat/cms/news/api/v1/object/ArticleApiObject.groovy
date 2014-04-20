/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.mayocat.addons.model.AddonField
import org.mayocat.addons.model.BaseProperties
import org.mayocat.addons.util.AddonUtils
import org.mayocat.cms.news.model.Article
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.model.Addon
import org.mayocat.rest.api.object.AddonApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.theme.ThemeDefinition

/**
 * Api object for an {@link Article}
 *
 * @version $Id$
 */
@CompileStatic
class ArticleApiObject extends BaseApiObject
{
    String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String model;

    Boolean published;

    String title;

    String content;

    DateTime publicationDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AddonApiObject> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonIgnore
    def withArticle(Article article, DateTimeZone tenantZone)
    {
        slug = article.slug
        title = article.title
        content = article.content
        published = article.published
        model = article.model.orNull()

        if (article.publicationDate != null) {
            publicationDate = new DateTime(article.publicationDate.time, tenantZone);
        }
    }

    @JsonIgnore
    Article toArticle(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition)
    {
        def article = new Article()
        article.with {
            slug = this.slug
            title = this.title
            content = this.content

            setModel this.model
        }

        if (addons) {
            List<Addon> articleAddons = []
            addons.each({ AddonApiObject addon ->
                Addon articleAddon = addon.toAddon()
                Optional<AddonField> definition = findAddonDefinition(articleAddon, platformSettings, themeDefinition)
                if (definition.isPresent() && !definition.get().properties.containsKey(BaseProperties.READ_ONLY)) {
                    // - Addons for which no definition can be found are ignored
                    // - Addons declared "Read only" are ignored : they can't be updated via this API !
                    articleAddons << articleAddon
                }
            })

            article.addons = articleAddons
        }

        article
    }

    @JsonIgnore
    def withAddons(List<Addon> articleAddons)
    {
        if (!addons) {
            addons = []
        }

        articleAddons.each({ Addon addon ->
            addons << AddonApiObject.forAddon(addon)
        })
    }

    @JsonIgnore
    def withEmbeddedImages(List<ImageApiObject> images)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        images.each({ ImageApiObject image ->
            if (image.featured) {
                featuredImage = image
            }
        })

        _embedded.images = images;

        if (featuredImage) {
            _embedded.featuredImage = featuredImage
        }
    }

    @JsonIgnore
    def withEmbeddedFeaturedImage(Image featuredImage)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        def imageApiObject = new ImageApiObject()
        imageApiObject.withImage(featuredImage)
        imageApiObject.featured = true
        _embedded.featuredImage = imageApiObject
    }

    private def Optional<AddonField> findAddonDefinition(Addon addonToFind, PlatformSettings platformSettings,
            Optional<ThemeDefinition> themeDefinition)
    {
        def option = Optional.absent();
        // 1. Find in platform
        option = AddonUtils.findAddonDefinition(addonToFind, platformSettings.addons);

        if (!option.isPresent() && themeDefinition.isPresent()) {
            // 2. Find in theme
            option = AddonUtils.findAddonDefinition(addonToFind, themeDefinition.get().addons);
        }
    }
}
