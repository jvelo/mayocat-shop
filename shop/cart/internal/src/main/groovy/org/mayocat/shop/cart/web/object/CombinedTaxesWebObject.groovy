/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.web.object

import groovy.transform.CompileStatic
import org.mayocat.shop.catalog.web.object.PriceWebObject

/**
 * @version $Id$
 */
@CompileStatic
class CombinedTaxesWebObject
{
    PriceWebObject vat

    List<TaxWebObject> others;
}
