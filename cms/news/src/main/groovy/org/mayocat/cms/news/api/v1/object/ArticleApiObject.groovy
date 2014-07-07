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
import org.mayocat.cms.news.model.Article
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.model.AddonGroup
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.theme.ThemeDefinition

import static org.mayocat.rest.api.object.AddonGroupApiObject.forAddonGroup
import static org.mayocat.rest.api.object.AddonGroupApiObject.toAddonGroupMap

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
    Map<String, AddonGroupApiObject> addons

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
            article.addons = toAddonGroupMap(addons, platformSettings, themeDefinition)
        }

        article
    }

    @JsonIgnore
    def withAddons(Map<String, AddonGroup> entityAddons) {
        if (!addons) {
            addons = [:]
        }

        entityAddons.values().each({ AddonGroup addon ->
            addons.put(addon.group, forAddonGroup(addon))
        })
    }

    @JsonIgnore
    def withEmbeddedImages(List<Image> images, UUID featuredImageId)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        def List<ImageApiObject> imageApiObjectList = [];

        images.each({ Image image ->
            ImageApiObject imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image)
            imageApiObject.featured = false

            if (image.attachment.id == featuredImageId) {
                featuredImage = imageApiObject
                imageApiObject.featured = true
            }
            imageApiObjectList << imageApiObject
        })

        _embedded.images = imageApiObjectList;

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
}
