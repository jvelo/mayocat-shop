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
import java.util.ArrayList;
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
import org.mayocat.image.model.Thumbnail;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.front.views.ErrorWebView;
import org.mayocat.shop.front.views.WebView;
import org.mayocat.shop.catalog.front.builder.ProductContextBuilder;
import org.mayocat.shop.catalog.meta.ProductEntity;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.builder.PaginationContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.theme.ThemeFileResolver;
import org.xwiki.component.annotation.Component;

import com.google.common.math.IntMath;

/**
 * @version $Id$
 */
@Component(ProductResource.PATH)
@Path(ProductResource.PATH)
@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ProductResource extends AbstractProductListWebViewResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + ProductEntity.PATH;

    @Inject
    private Provider<ProductStore> productStore;

    @GET
    public WebView getProducts(@QueryParam("page") @DefaultValue("1") Integer page, @Context UriInfo uriInfo)
    {
        final int currentPage = page < 1 ? 1 : page;
        Integer numberOfProductsPerPage =
                context.getTheme().getDefinition().getPaginationDefinition("products").getItemsPerPage();

        Integer offset = (page - 1) * numberOfProductsPerPage;
        Integer totalCount = this.productStore.get().countAllOnShelf();
        Integer totalPages = IntMath.divide(totalCount, numberOfProductsPerPage, RoundingMode.UP);

        Map<String, Object> context = new HashMap<>();
        context.put(ContextConstants.PAGE_TITLE, "All products");

        List<Product> products = this.productStore.get().findAllOnShelf(numberOfProductsPerPage, offset);
        context.put("products",
                createProductListContext(currentPage, totalPages, products, new PaginationContextBuilder.UrlBuilder()
                {
                    public String build(int page)
                    {
                        return MessageFormat.format("/products/?page={0}", page);
                    }
                }));

        return new WebView().template("products.html").data(context);
    }

    @Path("{slug}")
    @GET
    public WebView getProduct(final @PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        final Product product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return new ErrorWebView().status(404);
        }

        List<org.mayocat.shop.catalog.model.Collection> collections =
                collectionStore.get().findAllForProduct(product);
        product.setCollections(collections);
        if (collections.size() > 0) {
            // Here we take the first collection in the list, but in the future we should have the featured
            // collection as the parent entity of this product
            product.setFeaturedCollection(collections.get(0));
        }

        Map<String, Object> context = new HashMap<>();

        context.put(ContextConstants.PAGE_TITLE, product.getTitle());
        context.put(ContextConstants.PAGE_DESCRIPTION, product.getDescription());

        ThemeDefinition theme = this.context.getTheme().getDefinition();

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(product);
        List<Image> images = new ArrayList<>();
        for (Attachment attachment : attachments) {
            if (isImage(attachment)) {
                List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                Image image = new Image(entityLocalizationService.localize(attachment), thumbnails);
                images.add(image);
            }
        }

        ProductContextBuilder builder = new ProductContextBuilder(urlFactory, configurationService,
                entityLocalizationService, theme, themeFileResolver);
        Map<String, Object> productContext = builder.build(entityLocalizationService.localize(product), images);

        context.put("product", productContext);

        return new WebView().template("product.html").model(product.getModel()).data(context);
    }
}
