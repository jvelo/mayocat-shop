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
