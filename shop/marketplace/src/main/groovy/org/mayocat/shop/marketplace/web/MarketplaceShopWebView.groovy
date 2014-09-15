/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web

import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.accounts.store.TenantStore
import org.mayocat.rest.Resource
import org.mayocat.shop.front.views.WebView
import org.mayocat.shop.marketplace.web.object.MarketplaceShopWebObject
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/marketplace/shops")
@Path("/marketplace/shops")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@CompileStatic
class MarketplaceShopWebView implements Resource
{
    @Inject
    Provider<TenantStore> tenantStore

    @GET
    def listShops()
    {
        def context = [:]

        List<Tenant> tenants = tenantStore.get().findAll(100, 0)
        List<MarketplaceShopWebObject> shops = []

        tenants.each({ Tenant tenant ->
            shops << new MarketplaceShopWebObject().withTenant(tenant)
        })

        context.put("shops", shops)

        return new WebView().data(context)
    }
}
