/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog;

import java.util.List;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.InvalidOperation;
import org.xwiki.component.annotation.Role;

/**
 * Regroup product, product variants and catalog operations.
 *
 * @version $Id$
 */
@Role
public interface CatalogService
{
    // Product operations

    Product createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateProduct(Product entity) throws EntityDoesNotExistException, InvalidEntityException;

    Product findProductBySlug(String slug);

    List<Product> findAllProducts(int number, int offset);

    /**
     * @return all products that does not belong to any collection
     */
    List<Product> findOrphanProducts();

    // Collection operations

    Collection createCollection(Collection entity) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateCollection(Collection entity) throws EntityDoesNotExistException, InvalidEntityException;

    /**
     * @param product the product to find the collections for
     * @return all the collections this product is listed in
     */
    List<Collection> findCollectionsForProduct(Product product);

    /**
     * @param collection the collection to find the products for
     * @return all the products this collection lists
     */
    List<Product> findProductsForCollection(Collection collection);

    /**
     * Adds a product to a collection
     *
     * @param collection the slug of the collection to add the product to
     * @param product the slug of the product to add
     */
    void addProductToCollection(String collection, String product) throws InvalidOperation;

    /**
     * Adds a product to a collection
     *
     * @param collection the slug of the collection to add the product to
     * @param product the slug of the product to add
     */
    void removeProductFromCollection(String collection, String product) throws InvalidOperation;

    enum InsertPosition
    {
        BEFORE,
        AFTER
    }

    /**
     * Moves a product relatively to another, and shifts other products as necessary.
     *
     * @param slugOfProductToMove the slug of the product to move
     * @param slugOfProductToMoveBeforeOf the slug of the product to move before of
     * @throws org.mayocat.store.InvalidMoveOperation when no sense can be made of move parameters
     * @see {@link #moveProductInCollection(org.mayocat.shop.catalog.model.Collection, String, String, InsertPosition)}
     *      Inserts the product before the relative target.
     */
    void moveProduct(String slugOfProductToMove, String slugOfProductToMoveBeforeOf)
            throws InvalidMoveOperation;

    /**
     * Moves a product relatively to another, and shifts other products as necessary.
     *
     * @param slugOfProductToMove the slug of the project to move
     * @param slugOfProductToMoveBeforeOf the slug of the product to move before of
     * @param position the relative insert position : before or after
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveProduct(String slugOfProductToMove, String slugOfProductToMoveBeforeOf, InsertPosition position)
            throws InvalidMoveOperation;

    /**
     * Moves a collection relatively to another, and shifts other collections as necessary.
     *
     * @param slugOfCollectionToMove the slug of the collection to move
     * @param slugOfCollectionToMoveBeforeOf the slug of the collection to move before of
     * @throws org.mayocat.store.InvalidMoveOperation when no sense can be made of move parameters
     * @see {@link #moveProductInCollection(org.mayocat.shop.catalog.model.Collection, String, String, InsertPosition)}
     *      Inserts the product before the relative target.
     */
    void moveCollection(String slugOfCollectionToMove, String slugOfCollectionToMoveBeforeOf)
            throws InvalidMoveOperation;

    /**
     * Moves a collection relatively to another, and shifts other collections as necessary.
     *
     * @param slugOfCollectionToMove the slug of the collection to move
     * @param slugOfCollectionToMoveBeforeOf the slug of the collection to move before of
     * @param position the relative insert position : before or after
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveCollection(String slugOfCollectionToMove, String slugOfCollectionToMoveBeforeOf, InsertPosition position)
            throws InvalidMoveOperation;

    /**
     * @param collection the collection in which to move the project in
     * @param slugOfProductToMove the slug of the project to move
     * @param slugOfProductToMoveBeforeOf the slug of the product to move before of
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     * @see {@link #moveProductInCollection(org.mayocat.shop.catalog.model.Collection, String, String, InsertPosition)}
     *      Inserts the product before the relative target.
     */
    void moveProductInCollection(Collection collection, String slugOfProductToMove, String slugOfProductToMoveBeforeOf)
            throws InvalidMoveOperation;

    /**
     * Move a product in a collection. This operation changes the position of a product within a collection, and shifts
     * other products within the same collection as necessary.
     *
     * @param collection the collection in which to move the project in
     * @param slugOfProductToMove the slug of the project to move
     * @param relativeSlug the slug of the product to move relative to
     * @param position the relative insert position : before or after
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveProductInCollection(Collection collection, String slugOfProductToMove, String relativeSlug,
            InsertPosition position) throws InvalidMoveOperation;

    Collection findCollectionBySlug(String slug);

    List<Collection> findAllCollections(int number, int offset);

    List<EntityAndCount<Collection>> findAllCollectionsWithProductCount();

    void deleteProduct(String productSlug) throws EntityDoesNotExistException;

    void deleteCollection(String collectionSlug) throws EntityDoesNotExistException;
}
