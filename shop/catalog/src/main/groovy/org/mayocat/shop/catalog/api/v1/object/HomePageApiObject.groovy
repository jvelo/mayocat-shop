package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic

/**
 * API object for the home page management. Contains a list of featured products.
 *
 * @version $Id$
 */
@CompileStatic
class HomePageApiObject
{
    List<ProductApiObject> featuredProducts;
}
