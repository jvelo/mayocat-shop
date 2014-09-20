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

    def withFeaturedImage(Tenant tenant, Image image, PlatformSettings platformSettings)
    {
        featuredImage = new MarketplaceImageWebObject()
        featuredImage.withImage(tenant, image, true, platformSettings)
    }
}
