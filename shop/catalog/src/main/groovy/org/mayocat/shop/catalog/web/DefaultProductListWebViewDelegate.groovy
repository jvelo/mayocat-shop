/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.addons.web.AddonsWebObjectBuilder
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.image.model.Image
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.rest.web.object.PaginationWebObject
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Collection
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.web.object.ProductListWebObject
import org.mayocat.shop.catalog.web.object.ProductWebObject
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject

/**
 * @version $Id$
 */
@CompileStatic
@Component
class DefaultProductListWebViewDelegate implements ProductListWebViewDelegate
{
    @Inject
    ConfigurationService configurationService

    @Inject
    WebContext context

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    AddonsWebObjectBuilder addonsWebObjectBuilder

    @Inject
    EntityLocalizationService entityLocalizationService

    ProductListWebObject buildProductListWebObject(int currentPage, Integer totalPages,
            List<EntityData<Product>> products, Closure<String> urlBuilder)
    {
        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(currentPage, totalPages, urlBuilder)

        new ProductListWebObject([
                pagination: pagination,
                list      : buildProductListListWebObject(products, Optional.absent())
        ])
    }

    List<ProductWebObject> buildProductListListWebObject(List<EntityData<Product>> productsData,
            Optional<Collection> collection)
    {
        List<ProductWebObject> list = []
        ThemeDefinition theme = this.context.theme?.definition

        productsData.each({ EntityData<Product> productData ->

            Product product = productData.entity
            List<Image> images = productData.getDataList(Image.class)

            ProductWebObject productWebObject = new ProductWebObject()
            productWebObject.
                    withProduct(entityLocalizationService.localize(product) as Product, urlFactory, themeFileResolver,
                            configurationService.getSettings(CatalogSettings.class),
                            configurationService.getSettings(GeneralSettings.class))
            productWebObject.withAddons(addonsWebObjectBuilder.build(productData))

            if (collection.isPresent()) {
                productWebObject.withCollection(collection.get(), urlFactory)
            } else if (product.collections.isLoaded() && product.collections.get().size() > 0) {
                productWebObject.withCollection(product.collections.get().get(0), urlFactory)
            }

            productWebObject.withImages(images as List<Image>, product.featuredImageId, Optional.fromNullable(theme))

            list << productWebObject
        })

        list
    }
}
