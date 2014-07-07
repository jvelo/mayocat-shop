/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.entity.EntityData
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.web.object.ProductListWebObject
import org.mayocat.shop.catalog.web.object.ProductWebObject
import org.xwiki.component.annotation.Role

/**
 * @version $Id$
 */
@CompileStatic
@Role
public interface ProductListWebViewDelegate
{
    ProductListWebObject buildProductListWebObject(int currentPage, Integer totalPages,
            List<EntityData<Product>> products, Closure<String> urlBuilder)

    List<ProductWebObject> buildProductListListWebObject(List<EntityData<Product>> productsData,
            Optional<org.mayocat.shop.catalog.model.Collection> collection)
}