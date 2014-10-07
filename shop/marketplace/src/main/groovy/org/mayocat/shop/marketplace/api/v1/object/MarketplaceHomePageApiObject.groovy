/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.cms.home.api.object.HomePageApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.marketplace.web.object.WithMarketplaceImages

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceHomePageApiObject extends HomePageApiObject implements WithMarketplaceImages
{
    List<ProductApiObject> featuredProducts = [];

    List<TenantApiObject> featuredTenants = [];
}
