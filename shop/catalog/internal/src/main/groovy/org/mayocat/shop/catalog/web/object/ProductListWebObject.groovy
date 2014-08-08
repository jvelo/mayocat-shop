/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic
import org.mayocat.rest.web.object.PaginationWebObject

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
