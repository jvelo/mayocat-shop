/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic

/**
 * Web object for a product variant (a {@link org.mayocat.shop.catalog.model.Product} with a parent set
 *
 * @version $Id$
 */
@CompileStatic
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
