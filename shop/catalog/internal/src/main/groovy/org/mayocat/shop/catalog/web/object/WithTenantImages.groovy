/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.rest.web.object.EntityImagesWebObject
import org.mayocat.rest.web.object.ImageWebObject
import org.mayocat.theme.ThemeDefinition

/**
 * @version $Id$
 */
@CompileStatic
trait WithTenantImages
{
    EntityImagesWebObject images

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
            if (!featuredImage) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = all.get(0)
            }
        } else {
            // Create placeholder image
            featuredImage = new ImageWebObject()
            featuredImage.withPlaceholderImage(true, theme)
            all = [featuredImage]
        }
        images = new EntityImagesWebObject([
                all     : all,
                featured: featuredImage
        ])
    }
}
