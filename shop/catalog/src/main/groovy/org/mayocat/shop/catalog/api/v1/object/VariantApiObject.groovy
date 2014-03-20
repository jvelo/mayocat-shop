package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic

/**
 * Represents a variant API object
 *
 * @version $Id$
 */
@CompileStatic
class VariantApiObject extends ProductApiObject
{
    Map<String, String> features;
}
