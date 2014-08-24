/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.Slugifier;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.InvalidOperation;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class DefaultCatalogService implements CatalogService
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<CollectionStore> collectionStore;

    @Inject
    private Slugifier slugifier;

    public Product createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException
    {
        if (Strings.isNullOrEmpty(entity.getSlug())) {
            entity.setSlug(this.slugifier.slugify(entity.getTitle()));
        }

        productStore.get().create(entity);

        return this.findProductBySlug(entity.getSlug());
    }

    public void updateProduct(Product entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.productStore.get().update(entity);
    }

    public Product findProductBySlug(String slug)
    {
        return this.productStore.get().findBySlug(slug);
    }

    public List<Product> findAllProducts(int number, int offset)
    {
        return this.productStore.get().findAll(number, offset);
    }

    public List<Product> findOrphanProducts()
    {
        return this.productStore.get().findOrphanProducts();
    }

    @Override
    public Collection createCollection(Collection entity) throws InvalidEntityException, EntityAlreadyExistsException
    {
        if (Strings.isNullOrEmpty(entity.getSlug())) {
            entity.setSlug(this.slugifier.slugify(entity.getTitle()));
        }
        this.collectionStore.get().create(entity);

        return this.findCollectionBySlug(entity.getSlug());
    }

    @Override
    public void updateCollection(Collection entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.collectionStore.get().update(entity);
    }

    @Override
    public List<Collection> findCollectionsForProduct(Product product)
    {
        return this.collectionStore.get().findAllForProduct(product);
    }

    @Override
    public List<Product> findProductsForCollection(Collection collection)
    {
        return this.productStore.get().findAllForCollection(collection);
    }

    @Override
    public void addProductToCollection(String collection, String product) throws InvalidOperation
    {
        Collection c = this.findCollectionBySlug(collection);
        Product p = this.findProductBySlug(product);
        if (p == null || c == null) {
            throw new InvalidOperation("Product or collection does not exist");
        }
        List<Collection> collections = this.collectionStore.get().findAllForProduct(p);
        if (collections.contains(c)) {
            // Already has it : nothing to do
            return;
        }
        this.collectionStore.get().addProduct(c, p);
    }

    @Override
    public void removeProductFromCollection(String collection, String product) throws InvalidOperation
    {
        Collection c = this.findCollectionBySlug(collection);
        Product p = this.findProductBySlug(product);
        if (p == null || c == null) {
            throw new InvalidOperation("Product or collection does not exist");
        }
        List<Collection> collections = this.collectionStore.get().findAllForProduct(p);
        if (!collections.contains(c)) {
            // It does not contain it : nothing to do
            return;
        }
        this.collectionStore.get().removeProduct(c, p);
    }

    @Override
    public void moveProduct(String slugOfProductToMove, String slugOfProductToMoveBeforeOf)
            throws InvalidMoveOperation
    {
        this.moveProduct(slugOfProductToMove, slugOfProductToMoveBeforeOf, InsertPosition.BEFORE);
    }

    @Override
    public void moveProduct(String slugOfProductToMove, String slugOfProductToRelativeTo,
            InsertPosition position) throws InvalidMoveOperation
    {
        this.productStore.get().moveProduct(slugOfProductToMove, slugOfProductToRelativeTo,
                position.equals(InsertPosition.AFTER) ? HasOrderedCollections.RelativePosition.AFTER :
                        HasOrderedCollections.RelativePosition.BEFORE);
    }

    @Override
    public void moveCollection(String slugOfCollectionToMove, String slugOfCollectionToMoveBeforeOf)
            throws InvalidMoveOperation
    {
        this.moveCollection(slugOfCollectionToMove, slugOfCollectionToMoveBeforeOf, InsertPosition.BEFORE);
    }

    @Override
    public void moveCollection(String slugOfCollectionToMove, String slugOfCollectionToMoveRelativeTo,
            InsertPosition position) throws InvalidMoveOperation
    {
        this.collectionStore.get().moveCollection(slugOfCollectionToMove, slugOfCollectionToMoveRelativeTo,
                position.equals(InsertPosition.AFTER) ? HasOrderedCollections.RelativePosition.AFTER :
                        HasOrderedCollections.RelativePosition.BEFORE);
    }

    @Override
    public Collection findCollectionBySlug(String slug)
    {
        return this.collectionStore.get().findBySlug(slug);
    }

    @Override
    public List<Collection> findAllCollections(int number, int offset)
    {
        return this.collectionStore.get().findAll(number, offset);
    }

    @Override
    public List<EntityAndCount<Collection>> findAllCollectionsWithProductCount()
    {
        return this.collectionStore.get().findAllWithProductCount();
    }

    @Override
    public void deleteProduct(String productSlug) throws EntityDoesNotExistException
    {
        Product product = this.findProductBySlug(productSlug);

        if (product == null) {
            throw new EntityDoesNotExistException();
        }

        this.productStore.get().delete(product);
    }

    @Override
    public void deleteCollection(String collectionSlug) throws EntityDoesNotExistException
    {
        Collection collection = this.findCollectionBySlug(collectionSlug);

        if (collection == null) {
            throw new EntityDoesNotExistException();
        }

        this.collectionStore.get().delete(collection);
    }

    @Override
    public void moveProductInCollection(Collection collection, String slugOfProductToMove, String relativeSlug)
            throws InvalidMoveOperation
    {
        this.moveProductInCollection(collection, slugOfProductToMove, relativeSlug, InsertPosition.BEFORE);
    }

    @Override
    public void moveProductInCollection(Collection collection, String slugOfProductToMove, String relativeSlug,
            InsertPosition insertPosition) throws InvalidMoveOperation
    {
        this.collectionStore.get().moveProductInCollection(collection, slugOfProductToMove, relativeSlug,
                insertPosition.equals(InsertPosition.AFTER) ? HasOrderedCollections.RelativePosition.AFTER :
                        HasOrderedCollections.RelativePosition.BEFORE);
    }
}
