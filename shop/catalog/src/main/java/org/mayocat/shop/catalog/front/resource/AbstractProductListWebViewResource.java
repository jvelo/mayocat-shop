/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.WebContext;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Attachment;
import org.mayocat.shop.catalog.front.builder.ProductContextBuilder;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.front.builder.PaginationContextBuilder;
import org.mayocat.shop.front.resources.AbstractWebViewResource;
import org.mayocat.shop.front.util.WebDataHelper;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.ThemeFileResolver;
import org.mayocat.url.EntityURLFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static org.mayocat.shop.front.util.WebDataHelper.isEntityFeaturedImage;
import static org.mayocat.shop.front.util.WebDataHelper.isThumbnailOfAttachment;

/**
 * Base class for front resources with product lists.
 *
 * @version $Id$
 */
public class AbstractProductListWebViewResource extends AbstractWebViewResource
{
    @Inject
    protected ConfigurationService configurationService;

    @Inject
    protected Provider<AttachmentStore> attachmentStore;

    @Inject
    protected Provider<ThumbnailStore> thumbnailStore;

    @Inject
    protected Provider<CollectionStore> collectionStore;

    @Inject
    protected WebContext context;

    @Inject
    protected EntityURLFactory urlFactory;

    @Inject
    protected ThemeFileResolver themeFileResolver;

    @Inject
    protected EntityLocalizationService entityLocalizationService;

    protected Map<String, Object> createProductListContext(List<Product> products)
    {
        final Map<String, Object> productsContext = Maps.newHashMap();
        final List<Map<String, Object>> productsListContext = Lists.newArrayList();

        java.util.Collection<UUID> featuredImageIds = Collections2.transform(products,
                WebDataHelper.ENTITY_FEATURED_IMAGE);
        List<UUID> ids = new ArrayList<>(Collections2.filter(featuredImageIds, Predicates.notNull()));
        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;
        if (ids.isEmpty()) {
            allImages = Collections.emptyList();
            allThumbnails = Collections.emptyList();
        } else {
            allImages = this.attachmentStore.get().findByIds(ids);
            allThumbnails = this.thumbnailStore.get().findAllForIds(ids);
        }

        ProductContextBuilder builder = new ProductContextBuilder(urlFactory, configurationService,
                entityLocalizationService, this.context.getTheme().getDefinition(), themeFileResolver);

        for (final Product product : products) {
            java.util.Collection<Attachment> attachments =
                    Collections2.filter(allImages, isEntityFeaturedImage(product));
            List<Image> images = new ArrayList<>();
            for (final Attachment attachment : attachments) {
                java.util.Collection<Thumbnail> thumbnails =
                        Collections2.filter(allThumbnails, isThumbnailOfAttachment(attachment));
                Image image = new Image(entityLocalizationService.localize(attachment), new ArrayList<>(thumbnails));
                images.add(image);
            }
            List<org.mayocat.shop.catalog.model.Collection> productCollections =
                    collectionStore.get().findAllForProduct(product);
            product.setCollections(productCollections);
            if (productCollections.size() > 0) {
                // Here we take the first collection in the list, but in the future we should have the featured
                // collection as the parent entity of this product
                product.setFeaturedCollection(productCollections.get(0));
            }
            Map<String, Object> productContext = builder.build(entityLocalizationService.localize(product), images);
            productsListContext.add(productContext);
        }

        productsContext.put("list", productsListContext);
        return productsContext;
    }

    protected Map<String, Object> createProductListContext(int currentPage, Integer totalPages,
            List<Product> products, PaginationContextBuilder.UrlBuilder urlBuilder)
    {
        Map<String, Object> productsContext = createProductListContext(products);

        PaginationContextBuilder paginationContextBuilder = new PaginationContextBuilder();
        productsContext.put("pagination", paginationContextBuilder.build(currentPage, totalPages, urlBuilder));

        return productsContext;
    }
}
