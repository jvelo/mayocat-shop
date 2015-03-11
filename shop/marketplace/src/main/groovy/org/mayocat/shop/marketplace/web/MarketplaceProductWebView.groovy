/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web

import com.google.common.base.Strings
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.rest.Resource
import org.mayocat.rest.web.object.PaginationWebObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.shop.marketplace.model.EntityAndTenant
import org.mayocat.shop.marketplace.store.MarketplaceProductStore
import org.mayocat.shop.marketplace.web.delegate.WithProductWebObjectBuilder
import org.mayocat.shop.marketplace.web.object.MarketplaceProductListWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceProductWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceSearchResultWebObject
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import java.text.MessageFormat

/**
 * @version $Id$
 */
@Component("/marketplace/products")
@Path("/marketplace/products")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@CompileStatic
class MarketplaceProductWebView implements Resource, WithProductWebObjectBuilder
{
    @Inject
    MarketplaceProductStore productStore

    @Inject
    EntityDataLoader dataLoader

    // TODO pagination
    @GET
    @Path("search")
    def searchProducts(@QueryParam("q") String query, @QueryParam("page") @DefaultValue("1") Integer page)
    {
        if (Strings.isNullOrEmpty(query)) {
            return new ErrorWebView().status(404)
        }
        List<Product> products = productStore.findAllOnShelfWithTitleLike(query, 200, 0)
        List<EntityData<Product>> productsData = dataLoader.
                load(products, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        def productList = [] as List<MarketplaceProductWebObject>
        productsData.each({ EntityData<Product> productData ->
            def tenant = productData.getData(Tenant.class).orNull()
            MarketplaceProductWebObject productWebObject = buildProductWebObject(tenant, productData)
            productList << productWebObject
        })

        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(0, 0, { Integer p ->
            MessageFormat.format("/marketplace/products/search?query={0}&page={1}", query, p);
        })

        MarketplaceSearchResultWebObject result = new MarketplaceSearchResultWebObject()
        result.query = query
        result.products = new MarketplaceProductListWebObject(
                pagination: pagination,
                list: productList
        );

        def context = [:] as Map<String, Object>
        context.put("searchResults", result)
        new WebView().data(context)
    }
}
