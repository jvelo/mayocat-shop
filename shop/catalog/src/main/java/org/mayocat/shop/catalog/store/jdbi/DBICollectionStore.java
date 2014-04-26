/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.jdbi;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.model.EntityAndCount;
import org.mayocat.model.event.EntityCreatedEvent;
import org.mayocat.model.event.EntityCreatingEvent;
import org.mayocat.model.event.EntityUpdatedEvent;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.ProductCollection;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.mayocat.store.rdbms.dbi.MoveEntityInListOperation;

import mayoapp.dao.CollectionDAO;
import mayoapp.dao.ProductDAO;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = { "jdbi", "default" })
public class DBICollectionStore extends DBIEntityStore implements CollectionStore, Initializable
{
    private static final String COLLECTION_TABLE_NAME = "collection";

    public static final String COLLECTION_POSITION = "collection.position";

    private CollectionDAO dao;

    private ProductDAO productDao;

    public Collection create(Collection collection) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(collection.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        getObservationManager().notify(new EntityCreatingEvent(), collection);

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        collection.setId(entityId);

        this.dao.createEntity(collection, COLLECTION_TABLE_NAME, getTenant());
        Integer lastIndex = this.dao.lastPosition(getTenant());
        this.dao.create(lastIndex == null ? 0 : ++lastIndex, collection);
        // this.dao.insertTranslations(entityId, collection.getTranslations());

        this.dao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), collection);

        return collection;
    }

    public void update(Collection collection) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        Collection originalCollection = this.dao.findBySlug(collection.getSlug(), getTenant());
        if (originalCollection == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }
        collection.setId(originalCollection.getId());
        Integer updatedRows = this.dao.update(collection);

        if (collection.getLocalizedVersions() != null && !collection.getLocalizedVersions().isEmpty()) {
            Map<Locale, Map<String, Object>> localizedVersions = collection.getLocalizedVersions();
            for (Locale locale : localizedVersions.keySet()) {
                this.dao.createOrUpdateTranslation(collection.getId(), locale, localizedVersions.get(locale));
            }
        }

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating collection");
        }

        getObservationManager().notify(new EntityUpdatedEvent(), collection);
    }

    @Override
    public void delete(@Valid Collection entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteEntityEntityById(COLLECTION_TABLE_NAME, entity.getId());
        updatedRows += this.dao.detachChildren(entity.getId());
        updatedRows += this.dao.deleteEntityAndChildrenById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete collection");
        }
    }

    public void moveCollection(String collectionToMove, String collectionToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        this.dao.begin();

        List<Collection> allCollections = this.findAll();
        MoveEntityInListOperation<Collection> moveOp =
                new MoveEntityInListOperation<Collection>(allCollections, collectionToMove,
                        collectionToMoveRelativeTo, relativePosition);

        if (moveOp.hasMoved()) {
            this.dao.updatePositions(COLLECTION_TABLE_NAME, moveOp.getEntities(), moveOp.getPositions());
        }

        this.dao.commit();
    }

    @Override
    public void moveProductInCollection(Collection collection, String productToMove, String productToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        this.dao.begin();

        List<Product> categoryProducts = this.productDao.findAllForCollection(collection);

        MoveEntityInListOperation<Product> moveOp =
                new MoveEntityInListOperation<>(categoryProducts, productToMove,
                        productToMoveRelativeTo, relativePosition);

        if (moveOp.hasMoved()) {
            this.dao.updateProductPosition(moveOp.getEntities(), moveOp.getPositions());
        }

        this.dao.commit();
    }

    @Override
    public List<EntityAndCount<Collection>> findAllWithProductCount()
    {
        return this.dao.findAllWithProductCount(getTenant());
    }

    public void addProduct(Collection collection, Product product)
    {
        this.dao.begin();
        Integer position = this.dao.lastProductPosition(collection);
        if (position == null) {
            position = 0;
        } else {
            position += 1;
        }
        this.dao.addProduct(collection, product, position);
        this.dao.commit();
    }

    public void removeProduct(Collection collection, Product product)
    {
        this.dao.removeProduct(collection, product);
    }

    public List<Collection> findAllForProduct(Product product)
    {
        return this.dao.findAllForProduct(product);
    }

    public List<Collection> findAllForProductIds(List<UUID> ids)
    {
        if (ids == null || ids.size() <= 0) {
            return Collections.emptyList();
        }
        return this.dao.findAllForProductIds(ids);
    }

    public List<ProductCollection> findAllProductsCollectionsForIds(List<UUID> ids)
    {
        if (ids == null || ids.size() <= 0) {
            return Collections.emptyList();
        }
        return this.dao.findAllProductsCollectionsForIds(ids);
    }

    public List<Collection> findAll()
    {
        return this.dao.findAll(COLLECTION_TABLE_NAME, COLLECTION_POSITION, getTenant());
    }

    public List<Collection> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(COLLECTION_TABLE_NAME, COLLECTION_POSITION, getTenant(), number, offset);
    }

    @Override
    public List<Collection> findByIds(List<UUID> ids)
    {
        return this.dao.findByIds(COLLECTION_TABLE_NAME, ids);
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(COLLECTION_TABLE_NAME, getTenant());
    }

    public Collection findById(UUID id)
    {
        return this.dao.findById(COLLECTION_TABLE_NAME, id);
    }

    public Collection findBySlug(String slug)
    {
        return this.dao.findBySlug(COLLECTION_TABLE_NAME, slug, getTenant());
    }

    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(CollectionDAO.class);
        this.productDao = this.getDbi().onDemand(ProductDAO.class);
        super.initialize();
    }
}
