package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.BasePaginatedListApiObject

/**
 * Represents a list of products
 *
 * @version $Id$
 */
@CompileStatic
class ProductListApiObject extends BasePaginatedListApiObject
{
    List<ProductApiObject> products;
}
