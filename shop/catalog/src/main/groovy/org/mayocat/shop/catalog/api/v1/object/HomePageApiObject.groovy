/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
