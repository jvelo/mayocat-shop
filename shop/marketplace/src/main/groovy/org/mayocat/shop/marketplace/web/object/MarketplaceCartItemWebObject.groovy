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
import org.mayocat.shop.cart.web.object.AbstractCartItemWebObject

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceCartItemWebObject extends AbstractCartItemWebObject
{
    MarketplaceImageWebObject featuredImage

    MarketplaceShopWebObject shop

    MarketplaceProductWebObject product

    def withFeaturedImage(Tenant tenant, Image image, PlatformSettings platformSettings)
    {
        featuredImage = new MarketplaceImageWebObject()
        featuredImage.withImage(tenant, image, true, platformSettings)
    }
}
