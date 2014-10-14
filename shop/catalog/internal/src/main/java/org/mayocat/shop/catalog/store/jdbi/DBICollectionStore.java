/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.jdbi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.Entity;
import org.mayocat.model.EntityAndCount;
import org.mayocat.model.EntityAndParent;
import org.mayocat.model.PositionedEntity;
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

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import static org.mayocat.addons.util.AddonUtils.asMap;

@Component(hints = { "jdbi", "default" })
public class DBICollectionStore extends DBIEntityStore implements CollectionStore, Initializable
{
    private static final String COLLECTION_TABLE_NAME = "collection";

    public static final String COLLECTION_POSITION = "collection.position";

    private CollectionDAO dao;

    private ProductDAO productDao;

    public Collection create(Collection collection) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(collection.getSlug(), getTenant(), collection.getParentId()) != null) {
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
        this.dao.createOrUpdateAddons(collection);

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

        this.dao.createOrUpdateAddons(collection);

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
    public void updateCollectionTree(List<PositionedEntity<Collection>> positionedCollections)
    {
        this.dao.begin();

        List<Collection> collections = FluentIterable.from(positionedCollections)
                .transform(new Function<PositionedEntity<Collection>, Collection>()
                {
                    public Collection apply(PositionedEntity<Collection> input)
                    {
                        return input.getEntity();
                    }
                }).toList();

        List<Integer> positions = FluentIterable.from(positionedCollections)
                .transform(new Function<PositionedEntity<Collection>, Integer>()
                {
                    public Integer apply(PositionedEntity<Collection> input)
                    {
                        return input.getPosition();
                    }
                }).toList();

        this.dao.updateCollectionPosition(collections, positions);
        this.dao.updateCollectionParent(collections);

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

    @Override
    public void addEntityToCollection(Collection collection, Entity entity)
    {
        // FIXME: check if collection already has entity

        this.dao.begin();
        String path = getPath(collection);
        Integer position = this.dao.lastEntityPosition(collection);
        if (position == null) {
            position = 0;
        } else {
            position += 1;
        }
        this.dao.addEntityToCollection(collection, path, entity, position);
        this.dao.commit();
    }

    @Override
    public void removeEntityFromCollection(Collection collection, Entity entity)
    {
        this.dao.removeEntityFromCollection(collection, entity);
    }

    @Override
    public EntityAndParent<Collection> findBySlugs(String... slugsArray)
    {
        Stack<String> slugs = new Stack<>();
        slugs.addAll(Arrays.asList(slugsArray));

        List<org.mayocat.shop.catalog.model.Collection> chain = Lists.newArrayList();

        while (slugs.size() > 0) {
            String slug = slugs.remove(0);
            Collection collection = findBySlug(slug, chain.size() > 0 ? chain.get(0).getId() : null);
            if (collection == null) {
                // If the chain is broken, the collection is not found
                return null;
            }
            chain.add(0, collection);
        }

        return getCollectionChain(chain);
    }

    @Override
    public List<EntityAndParent<Collection>> findAllForEntity(Entity entity)
    {
        List<Collection> collections = this.dao.findCollectionsForEntity(entity);
        return getEntityAndParents(collections);
    }

    @Override
    public List<EntityAndParent<Collection>> findAllChildrenOfCollection(Collection entity)
    {
        List<Collection> collections = this.dao.findAllChildrenOfCollection(entity);
        return getEntityAndParents(collections);
    }

    private EntityAndParent<Collection> getCollectionChain(List<Collection> chain)
    {
        EntityAndParent<Collection> root = null;
        while (chain.size() > 0) {
            Collection c = chain.remove(chain.size() - 1);
            root = new EntityAndParent<>(root, c);
        }
        return root;
    }

    public List<Collection> findAllForProduct(Product product)
    {
        return AddonsHelper.withAddons(this.dao.findAllForProduct(product), dao);
    }

    public List<Collection> findAllForProductIds(List<UUID> ids)
    {
        if (ids == null || ids.size() <= 0) {
            return Collections.emptyList();
        }
        return AddonsHelper.withAddons(this.dao.findAllForProductIds(ids), dao);
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
        return AddonsHelper.withAddons(this.dao.findAll(COLLECTION_TABLE_NAME, COLLECTION_POSITION, getTenant()), dao);
    }

    public List<Collection> findAll(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(
                this.dao.findAll(COLLECTION_TABLE_NAME, COLLECTION_POSITION, getTenant(), number, offset), dao);
    }

    @Override
    public List<Collection> findAllOrderedByParentAndPosition()
    {
        return AddonsHelper.withAddons(this.dao.findAllOrderedByParentAndPosition(getTenant()), dao);
    }

    @Override
    public List<Collection> findByIds(List<UUID> ids)
    {
        return AddonsHelper.withAddons(this.dao.findByIds(COLLECTION_TABLE_NAME, ids), dao);
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(COLLECTION_TABLE_NAME, getTenant());
    }

    public Collection findById(UUID id)
    {
        Collection collection = this.dao.findById(COLLECTION_TABLE_NAME, id);
        if (collection != null) {
            List<AddonGroup> addons = this.dao.findAddons(collection);
            collection.setAddons(asMap(addons));
        }
        return collection;
    }

    public Collection findBySlug(String slug)
    {
        Collection collection = this.dao.findBySlug(COLLECTION_TABLE_NAME, slug, getTenant());
        if (collection != null) {
            List<AddonGroup> addons = this.dao.findAddons(collection);
            collection.setAddons(asMap(addons));
        }
        return collection;
    }

    @Override
    public Collection findBySlug(String slug, UUID parentId)
    {
        Collection collection = this.dao.findBySlug(COLLECTION_TABLE_NAME, slug, getTenant(), parentId);
        if (collection != null) {
            List<AddonGroup> addons = this.dao.findAddons(collection);
            collection.setAddons(asMap(addons));
        }
        return collection;
    }

    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(CollectionDAO.class);
        this.productDao = this.getDbi().onDemand(ProductDAO.class);
        super.initialize();
    }

    // Helpers
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Compute the ltree path of a collection, which is the UUID chain (where dashes are replaced by underscores since
     * dashes are not allowed in ltree pathes) of parents leading to that collection (included).
     *
     * @param collection the collection for which to get the path
     * @return the computed path
     */
    private String getPath(Collection collection)
    {
        Collection parent = collection;
        String path = collection.getId().toString().replace('-', '_');
        while (parent.getParentId() != null) {
            parent = findById(parent.getParentId());
            if (parent == null) {
                throw new RuntimeException("Broken collection path chain in database");
            }
            path = parent.getId().toString().replace('-', '_') + '.' + path;
        }
        return path;
    }

    /**
     * Get a list of entity and parents from a list of collections
     */
    private List<EntityAndParent<Collection>> getEntityAndParents(List<Collection> collections)
    {
        List<EntityAndParent<Collection>> result = Lists.newArrayList();

        for (Collection collection : collections) {
            List<Collection> chain = Lists.newArrayList();
            chain.add(collection);
            Collection parent = collection;
            while (parent.getParentId() != null) {
                parent = this.findById(parent.getParentId());
                chain.add(parent);
            }
            EntityAndParent<Collection> collectionChain = getCollectionChain(chain);
            result.add(collectionChain);

        }
        return result;
    }

}
