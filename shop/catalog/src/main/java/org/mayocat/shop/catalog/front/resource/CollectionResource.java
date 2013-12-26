/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.resource;

import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.image.model.Image;
import org.mayocat.shop.front.views.ErrorWebView;
import org.mayocat.shop.front.views.WebView;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.front.builder.CollectionContextBuilder;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.builder.PaginationContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.Resource;
import org.mayocat.theme.ThemeDefinition;
import org.xwiki.component.annotation.Component;

import com.google.common.math.IntMath;

/**
 * @version $Id$
 */
@Component(CollectionResource.PATH)
@Path(CollectionResource.PATH)
@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CollectionResource extends AbstractProductListWebViewResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + CollectionEntity.PATH;

    @Inject
    private CatalogService catalogService;

    @Inject
    private Provider<ProductStore> productStore;

    @GET
    @Path("{slug}")
    public WebView getCollection(@PathParam("slug") String slug, @QueryParam("page") @DefaultValue("1") Integer page)
    {
        final Collection collection = catalogService.findCollectionBySlug(slug);
        if (collection == null) {
            return new ErrorWebView().status(404);
        }

        final int currentPage = page < 1 ? 1 : page;

        Map<String, Object> context = new HashMap<>();

        context.put(PAGE_TITLE, collection.getTitle());
        context.put(PAGE_DESCRIPTION, collection.getDescription());

        ThemeDefinition theme = this.context.getTheme().getDefinition();

        Integer numberOfProductsPerPage =
                this.context.getTheme().getDefinition().getPaginationDefinition("collection").getItemsPerPage();

        Integer offset = (page - 1) * numberOfProductsPerPage;
        Integer totalCount = this.productStore.get().countAllForCollection(collection);
        Integer totalPages = IntMath.divide(totalCount, numberOfProductsPerPage, RoundingMode.UP);

        List<Product> products = productStore.get().findForCollection(collection, numberOfProductsPerPage, offset);

        CollectionContextBuilder builder = new CollectionContextBuilder(urlFactory, theme);
        Map<String, Object> collectionContext =
                builder.build(entityLocalizationService.localize(collection), Collections.<Image>emptyList());

        collectionContext.put("products", createProductListContext(currentPage, totalPages, products,
                new PaginationContextBuilder.UrlBuilder()
                {
                    public String build(int page)
                    {
                        return MessageFormat.format("/collections/{0}/?page={1}", collection.getSlug(), page);
                    }
                }));

        // TODO get the collection images
        context.put("collection", collectionContext);

        return new WebView().template("collection.html").data(context);
    }
}
