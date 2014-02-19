package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Web object for a product variant (a {@link org.mayocat.shop.catalog.model.Product} with a parent set
 *
 * @version $Id$
 */
class ProductVariantWebObject
{
    String title;

    String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PriceWebObject unitPrice

    // available -> for sale and in stock
    // not_for_sale
    // out_of_stock
    String availability

    List<ProductVariantFeatureWebObject> features;
}
