/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Feature;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.EntityStore;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore extends Store<Product, UUID>, HasOrderedCollections, EntityStore
{
    Product findBySlug(String slug);

    void moveProduct(String productToMove, String productToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;

    void updatePosition(Integer position, Product product);

    List<Product> findOrphanProducts();

    Integer countAllForCollection(Collection collection);

    List<Product> findForCollection(Collection collection, Integer number, Integer offset);

    List<Product> findAllForCollection(Collection collection);

    List<Product> findAllOnShelf(Integer number, Integer offset);

    Integer countAllOnShelf();

    List<Product> findAllNotVariants(Integer number, Integer offset);

    Integer countAllNotVariants();

    List<Product> findAllWithTitleLike(String title, Integer number, Integer offset);

    Integer countAllWithTitleLike(String title);

    List<Feature> findFeatures(Product product);

    List<Feature> findFeatures(Product product, String feature);

    Feature findFeature(Product product, String feature, String featureSlug);

    List<Product> findVariants(Product product);

    Product findVariant(Product product, String variantSlug);

    Feature createFeature(Feature feature) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateStock(UUID productId, Integer stockOffset) throws EntityDoesNotExistException;
}
