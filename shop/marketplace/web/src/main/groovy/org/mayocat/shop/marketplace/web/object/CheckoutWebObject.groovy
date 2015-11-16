/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.object

import groovy.transform.CompileStatic

/**
 * @version $Id$
 */
@CompileStatic
class CheckoutWebObject
{
    /**
     * Additional information given for this order by the customer
     */

    String additionalInformation

    /**
     * Extra custom JSON data for this order that can be send by the API client
     */
    Map<String, Object> extraData
}
