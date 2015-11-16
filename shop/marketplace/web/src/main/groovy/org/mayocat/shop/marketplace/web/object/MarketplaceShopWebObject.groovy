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
import org.mayocat.accounts.model.Tenant
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceShopWebObject implements WithMarketplaceImages
{
    String slug

    String name

    String description

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map <String, Object> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MarketplaceProductWebObject> products

    MarketplaceShopWebObject withTenant(Tenant tenant)
    {
        name = tenant.name
        description = tenant.description
        slug = tenant.slug

        this
    }

    MarketplaceShopWebObject withAddons(Map<String, Object> addons) {
        this.addons = addons

        this
    }


}
