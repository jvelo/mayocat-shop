/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.object

import groovy.transform.CompileStatic
import org.mayocat.model.EntityAndParent
import org.mayocat.rest.web.object.PaginationWebObject
import org.mayocat.shop.catalog.model.Collection
import org.mayocat.shop.catalog.web.object.AbstractCollectionWebObject
import org.mayocat.url.EntityURLFactory

import java.text.MessageFormat

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceCollectionWebObject extends AbstractCollectionWebObject implements WithMarketplaceImages
{
    MarketplaceProductListWebObject products

    List<MarketplaceCollectionWebObject> children

    List<String> slugs

    MarketplaceCollectionWebObject withCollection(Collection collection, EntityURLFactory urlFactory,
            List<String> slugs)
    {
        withCollection(collection, urlFactory)
        this.slugs = slugs

        this
    }

    MarketplaceCollectionWebObject withChildren(List<MarketplaceCollectionWebObject> children)
    {
        this.children = children

        this
    }

    MarketplaceCollectionWebObject withProducts(List<MarketplaceProductWebObject> productList, Integer currentPage,
            Integer totalPages)
    {
        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(currentPage, totalPages, { Integer page ->
            MessageFormat.format("/collections/{0}/?page={1}", slug, page);
        })

        products = new MarketplaceProductListWebObject([
                list      : productList,
                pagination: pagination
        ])

        this
    }
}
