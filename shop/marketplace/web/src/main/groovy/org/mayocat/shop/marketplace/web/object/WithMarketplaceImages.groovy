/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.object

import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image

/**
 * @version $Id$
 */
@CompileStatic
trait WithMarketplaceImages
{
    MarketplaceEntityImagesWebObject images

    def withImages(Tenant tenant, List<Image> imagesList, UUID featuredImageId, PlatformSettings platformSettings)
    {
        List<MarketplaceImageWebObject> all = [];
        MarketplaceImageWebObject featuredImage;

        if (imagesList.size() > 0) {
            for (Image image : imagesList) {
                def featured = image.attachment.id.equals(featuredImageId)
                MarketplaceImageWebObject imageWebObject = new MarketplaceImageWebObject();
                imageWebObject.withImage(tenant, image, featured, platformSettings)
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
            // Placeholder
        }
        images = new MarketplaceEntityImagesWebObject([
                all     : all,
                featured: featuredImage
        ])
    }
}