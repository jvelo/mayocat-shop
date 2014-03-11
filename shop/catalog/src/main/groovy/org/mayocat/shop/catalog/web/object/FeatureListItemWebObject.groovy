package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object that holds the representation of an option of a feature (for example "S", or "XL" for a shirt size).
 *
 * @version $Id$
 */
@CompileStatic
class FeatureListItemWebObject
{
    String slug

    String title

    String url

    String availability
}
