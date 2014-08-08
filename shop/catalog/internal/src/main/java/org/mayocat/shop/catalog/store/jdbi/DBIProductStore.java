/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.jdbi;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.event.EntityCreatedEvent;
import org.mayocat.model.event.EntityCreatingEvent;
import org.mayocat.model.event.EntityUpdatedEvent;
import org.mayocat.model.event.EntityUpdatingEvent;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Feature;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.mayocat.store.rdbms.dbi.MoveEntityInListOperation;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import mayoapp.dao.FeatureDAO;
import mayoapp.dao.ProductDAO;

import static org.mayocat.addons.util.AddonUtils.asMap;

@Component(hints = { "jdbi", "default" })
public class DBIProductStore extends DBIEntityStore implements ProductStore, Initializable
{
    private static final String PRODUCT_POSITION = "product.position";

    private static final String PRODUCT_TABLE_NAME = "product";

    private static final String FEATURE_TABLE_NAME = "product_feature";

    private ProductDAO dao;

    private FeatureDAO featureDao;

    public Product create(Product product) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(product.getSlug(), getTenant(), product.getParentId()) != null) {
            throw new EntityAlreadyExistsException();
        }

        if (!product.getAddons().isLoaded()) {
            product.setAddons(new HashMap<String, AddonGroup>());
        }

        getObservationManager().notify(new EntityCreatingEvent(), product);

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        product.setId(entityId);

        if (product.getParentId() != null) {
            this.dao.createChildEntity(product, PRODUCT_TABLE_NAME, getTenant());
        } else {
            this.dao.createEntity(product, PRODUCT_TABLE_NAME, getTenant());
        }
        product.setId(entityId);
        Integer lastIndex;
        if (product.getParentId() == null) {
            lastIndex = this.dao.lastPosition(getTenant());
        } else {
            lastIndex = this.dao.lastPositionForVariant(product);
        }
        if (lastIndex == null) {
            lastIndex = 0;
        }
        this.dao.createProduct(lastIndex + 1, product);
        // this.dao.insertTranslations(entityId, product.getTranslations());
        this.dao.createOrUpdateAddons(product);

        this.dao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), product);

        return product;
    }

    public void update(Product product) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        Product originalProduct = this.findBySlug(product.getSlug(), product.getParentId());

        if (originalProduct == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }

        if (!product.getAddons().isLoaded()) {
            product.setAddons(originalProduct.getAddons().get());
        }
        getObservationManager().notify(new EntityUpdatingEvent(), product);

        product.setId(originalProduct.getId());
        Integer updatedRows = this.dao.updateProduct(product);
        this.dao.createOrUpdateAddons(product);

        if (product.getLocalizedVersions() != null && !product.getLocalizedVersions().isEmpty()) {
            Map<Locale, Map<String, Object>> localizedVersions = product.getLocalizedVersions();
            for (Locale locale : localizedVersions.keySet()) {
                this.dao.createOrUpdateTranslation(product.getId(), locale, localizedVersions.get(locale));
            }
        }

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating product");
        }

        getObservationManager().notify(new EntityUpdatedEvent(), product);
    }

    public void updatePosition(Integer position, Product product)
    {
        this.dao.updatePosition(position, product);
    }

    public void updateStock(UUID productId, Integer stockOffset) throws EntityDoesNotExistException
    {
        this.dao.begin();

        Product product = this.findById(productId);

        if (product == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }

        product.setStock((product.getStock() == null ? 0 : product.getStock()) + stockOffset);
        Integer updatedRows = this.dao.updateProduct(product);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating product");
        }

        getObservationManager().notify(new EntityUpdatedEvent(), product);
    }

    public void delete(@Valid Product entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteAddons(entity);
        updatedRows += this.dao.deleteProductFromCollections(entity.getId());
        updatedRows += this.dao.deleteEntityEntityById(PRODUCT_TABLE_NAME, entity.getId());
        updatedRows += this.dao.detachChildren(entity.getId());
        updatedRows += this.dao.deleteEntityAndChildrenById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete product");
        }
    }

    public void moveProduct(String productToMove, String productToMoveRelativeTo,
            HasOrderedCollections.RelativePosition relativePosition) throws InvalidMoveOperation
    {
        this.dao.begin();

        List<Product> allProducts = this.findAll();
        MoveEntityInListOperation<Product> moveOp =
                new MoveEntityInListOperation<>(allProducts, productToMove,
                        productToMoveRelativeTo, relativePosition);

        if (moveOp.hasMoved()) {
            this.dao.updatePositions(PRODUCT_TABLE_NAME, moveOp.getEntities(), moveOp.getPositions());
        }

        this.dao.commit();
    }

    public List<Product> findOrphanProducts()
    {
        return AddonsHelper.withAddons(this.dao.findOrphanProducts(getTenant()), this.dao);
    }

    public List<Product> findAll()
    {
        return AddonsHelper.withAddons(this.dao.findAll(PRODUCT_TABLE_NAME, PRODUCT_POSITION, getTenant()), this.dao);
    }

    public List<Product> findAll(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(
                this.dao.findAll(PRODUCT_TABLE_NAME, PRODUCT_POSITION, getTenant(), number, offset), this.dao);
    }

    public List<Product> findAllWithTitleLike(String title, Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.dao.findAllWithTitleLike(getTenant(), title, number, offset), this.dao);
    }

    public Integer countAllWithTitleLike(String title)
    {
        return this.dao.countAllWithTitleLike(getTenant(), title);
    }

    public List<Product> findByIds(List<UUID> ids)
    {
        return AddonsHelper.withAddons(this.dao.findByIds(PRODUCT_TABLE_NAME, ids), this.dao);
    }

    public Integer countAll()
    {
        return this.dao.countAll(PRODUCT_TABLE_NAME, getTenant());
    }

    public Product findBySlug(String slug, UUID parentId)
    {
        if (parentId == null) {
            return findBySlug(slug);
        }

        Product product = this.dao.findBySlug(PRODUCT_TABLE_NAME, slug, getTenant(), parentId);
        if (product != null) {
            List<AddonGroup> addons = this.dao.findAddons(product);
            product.setAddons(asMap(addons));
        }
        return product;
    }

    public Product findBySlug(String slug)
    {
        Product product = this.dao.findBySlug(PRODUCT_TABLE_NAME, slug, getTenant());
        if (product != null) {
            List<AddonGroup> addons = this.dao.findAddons(product);
            product.setAddons(asMap(addons));
        }
        return product;
    }

    public List<Product> findAllForCollection(Collection collection)
    {
        return AddonsHelper.withAddons(this.dao.findAllForCollection(collection), this.dao);
    }

    public List<Product> findAllOnShelf(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.dao.findAllOnShelf(getTenant(), number, offset), this.dao);
    }

    public Integer countAllOnShelf()
    {
        return this.dao.countAllOnShelf(getTenant());
    }

    public List<Product> findAllNotVariants(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.dao.findAllNotVariants(getTenant(), number, offset), this.dao);
    }

    public Integer countAllNotVariants()
    {
        return this.dao.countAllNotVariants(getTenant());
    }

    public List<Product> findForCollection(Collection collection, Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.dao.findForCollection(collection, number, offset), this.dao);
    }

    public Integer countAllForCollection(Collection collection)
    {
        return this.dao.countAllForCollection(collection);
    }

    public Product findById(UUID id)
    {
        Product product = this.dao.findById(PRODUCT_TABLE_NAME, id);
        if (product != null) {
            List<AddonGroup> addons = this.dao.findAddons(product);
            product.setAddons(asMap(addons));
        }
        return product;
    }

    public List<Feature> findFeatures(Product product)
    {
        return AddonsHelper.withAddons(this.featureDao.findAllForProduct(product), this.featureDao);
    }

    public List<Feature> findFeatures(Product product, final String feature)
    {
        return FluentIterable.from(findFeatures(product)).filter(new Predicate<Feature>()
        {
            public boolean apply(Feature input)
            {
                return input.getFeature().equals(feature);
            }
        }).toList();
    }

    public Feature findFeature(Product product, final String feature, final String featureSlug)
    {
        return FluentIterable.from(findFeatures(product)).filter(new Predicate<Feature>()
        {
            public boolean apply(Feature input)
            {
                return input.getFeature().equals(feature) && input.getFeatureSlug().equals(featureSlug);
            }
        }).first().orNull();
    }

    public List<Product> findVariants(Product product)
    {
        return AddonsHelper.withAddons(this.dao.findAllVariants(product), this.dao);
    }

    public Product findVariant(Product product, final String variantSlug)
    {
        return FluentIterable.from(findVariants(product)).filter(new Predicate<Product>()
        {
            public boolean apply(@Nullable Product input)
            {
                return input.getSlug().equals(variantSlug);
            }
        }).first().orNull();
    }

    public Feature createFeature(final Feature feature) throws InvalidEntityException, EntityAlreadyExistsException
    {
        if (feature.getParentId() == null) {
            throw new InvalidEntityException("Cannot create a feature without a parent product specified");
        }

        Product product = this.findById(feature.getParentId());
        if (product == null) {
            throw new InvalidEntityException("Specified parent product does not exist");
        }

        boolean exists = findFeature(product, feature.getFeature(), feature.getFeatureSlug()) != null;

        if (exists) {
            throw new EntityAlreadyExistsException();
        }

        if (!feature.getAddons().isLoaded()) {
            feature.setAddons(new HashMap<String, AddonGroup>());
        }

        getObservationManager().notify(new EntityCreatingEvent(), feature);

        this.featureDao.begin();

        UUID entityId = UUID.randomUUID();
        feature.setId(entityId);

        this.featureDao.createChildEntity(feature, FEATURE_TABLE_NAME, getTenant());
        this.featureDao.createFeature(feature);
        this.featureDao.createOrUpdateAddons(feature);

        this.featureDao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), feature);

        return feature;
    }

    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(ProductDAO.class);
        this.featureDao = this.getDbi().onDemand(FeatureDAO.class);
        super.initialize();
    }
}
