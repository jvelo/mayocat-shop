/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.rest.web.object.EntityImagesWebObject
import org.mayocat.rest.web.object.EntityModelWebObject
import org.mayocat.rest.web.object.ImageWebObject
import org.mayocat.rest.web.object.PaginationWebObject
import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.theme.ThemeDefinition
import org.mayocat.url.EntityURLFactory

import java.text.MessageFormat

/**
 * Web view for a {@link org.mayocat.shop.catalog.model.Collection}
 *
 * @version $Id$
 */
@CompileStatic
class CollectionWebObject
{
    String title

    String description

    String url

    String slug

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityModelWebObject model

    String template

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityImagesWebObject images

    @JsonInclude(JsonInclude.Include.NON_NULL)
    ProductListWebObject products

    def withCollection(org.mayocat.shop.catalog.model.Collection collection, EntityURLFactory urlFactory)
    {
        title = ContextUtils.safeString(collection.title)
        description = ContextUtils.safeHtml(collection.description)
        url = urlFactory.create(collection).path
        slug = collection.slug
    }

    def withProducts(List<ProductWebObject> productList, Integer currentPage, Integer totalPages)
    {
        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(currentPage, totalPages, { Integer page ->
            MessageFormat.format("/collections/{0}/?page={1}", slug, page);
        })

        products = new ProductListWebObject([
                list: productList,
                pagination: pagination
        ])
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
