package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object that represents a list of products. See {@link CollectionWebObject} for example.
 *
 * @version $Id$
 */
@CompileStatic
class ProductListWebObject
{
    List<ProductWebObject> list

    PaginationWebObject pagination
}
