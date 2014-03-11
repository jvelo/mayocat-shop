package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object that holds the representation of a feature of a product variant, for example size "s" for the variant
 * "S and red" for a shirt (which product type says it defines both color and size).
 *
 * @version $Id$
 */
@CompileStatic
class ProductVariantFeatureWebObject {

    String feature

    String slug
}
