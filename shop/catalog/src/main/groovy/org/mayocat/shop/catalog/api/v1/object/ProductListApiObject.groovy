package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.TypeChecked

/**
 * Represents a list of products
 *
 * @version $Id$
 */
@TypeChecked
class ProductListApiObject extends BasePaginatedListApiObject
{
    List<ProductApiObject> products;
}
