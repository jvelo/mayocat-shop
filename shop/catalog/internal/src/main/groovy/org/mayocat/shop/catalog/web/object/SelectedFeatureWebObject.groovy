/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
