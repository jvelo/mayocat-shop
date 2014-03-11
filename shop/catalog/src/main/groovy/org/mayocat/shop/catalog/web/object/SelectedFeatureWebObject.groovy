package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object that represents a product {@link org.mayocat.shop.catalog.model.Feature} that has been selected by a
 * prospective customer on a product page (before being added to the cart).
 *
 * @version $Id$
 */
@CompileStatic
class SelectedFeatureWebObject {

    String featureName;

    String featureSlug;

    String title;

    String slug;
}
