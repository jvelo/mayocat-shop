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

import org.mayocat.model.Entity;
import org.mayocat.model.EntityAndParent;
import org.mayocat.model.PositionedEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.ProductCollection;
import org.mayocat.store.EntityStore;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface CollectionStore extends Store<Collection, UUID>, EntityStore, HasOrderedCollections
{
    Collection findBySlug(String slug);

    Collection findBySlug(String slug, UUID parentId);

    @Deprecated
    void addProduct(Collection collection, Product product);

    @Deprecated
    void removeProduct(Collection c, Product p);

    void addEntityToCollection(Collection collection, Entity entity);

    void removeEntityFromCollection(Collection collection, Entity entity);

    EntityAndParent<Collection> findBySlugs(String... slugs);

    List<EntityAndParent<Collection>> findAllForEntity(Entity entity);

    List<EntityAndParent<Collection>> findAllChildrenOfCollection(Collection entity);

    void moveCollection(String collectionToMove, String collectionToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;

    void moveProductInCollection(Collection collection, String productToMove, String productToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation;

    void updateCollectionTree(List<PositionedEntity<Collection>> collections);

    List<EntityAndCount<Collection>> findAllWithProductCount();

    List<Collection> findAll();

    List<Collection> findAllOrderedByParentAndPosition();

    List<Collection> findAllForProduct(Product product);

    List<Collection> findAllForProductIds(List<UUID> ids);

    List<ProductCollection> findAllProductsCollectionsForIds(List<UUID> ids);
}