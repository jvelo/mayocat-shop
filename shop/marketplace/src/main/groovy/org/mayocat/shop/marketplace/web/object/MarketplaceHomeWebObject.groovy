package org.mayocat.shop.marketplace.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import org.mayocat.shop.catalog.web.object.ProductWebObject

/**
 * @version $Id$
 */
class MarketplaceHomeWebObject
{
    List<MarketplaceProductWebObject> featuredProducts;

    List<MarketplaceShopWebObject> featuredShops;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map <String, Object> addons
}
