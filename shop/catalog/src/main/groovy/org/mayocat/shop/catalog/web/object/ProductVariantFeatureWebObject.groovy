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
