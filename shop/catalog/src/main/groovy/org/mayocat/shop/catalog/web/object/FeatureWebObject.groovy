package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object for a {@link org.mayocat.shop.catalog.model.Feature}
 *
 * @version $Id$
 */
@CompileStatic
class FeatureWebObject {

    String slug

    String name

    List<FeatureListItemWebObject> options
}
