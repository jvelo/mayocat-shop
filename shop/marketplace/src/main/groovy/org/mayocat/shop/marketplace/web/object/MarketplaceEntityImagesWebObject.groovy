/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic
import org.mayocat.rest.web.object.ImageWebObject

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceEntityImagesWebObject
{
    MarketplaceImageWebObject featured

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MarketplaceImageWebObject> all
}
