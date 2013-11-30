/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.memory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.memory.AbstractPositionedEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link org.mayocat.shop.catalog.store.CollectionStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryCollectionStore extends AbstractPositionedEntityMemoryStore<Collection, CollectionPositionedEntity>
        implements CollectionStore
{
    private static Predicate<CollectionPositionedEntity> hasProduct(final Product product)
    {
        return new Predicate<CollectionPositionedEntity>()
        {
            public boolean apply(@Nullable CollectionPositionedEntity input)
            {
                return input.getProducts().contains(product);
            }
        };
    }

    private static Function<CollectionPositionedEntity, EntityAndCount<Collection>> entityAndCount =
            new Function<CollectionPositionedEntity, EntityAndCount<Collection>>()
            {
                @Nullable public EntityAndCount<Collection> apply(@Nullable CollectionPositionedEntity input)
                {
                    return new EntityAndCount<Collection>(input.getEntity(), Long.valueOf(input.getProducts().size()));
                }
            };

    protected CollectionPositionedEntity createForEntity(Collection entity, Integer position)
    {
        return new CollectionPositionedEntity(entity, position, new ArrayList<Product>());
    }

    public void addProduct(Collection collection, Product product)
    {
        findPositionedById(collection.getId()).getProducts().add(product);
    }

    @Override
    public void removeProduct(Collection collection, Product product)
    {
        findPositionedById(collection.getId()).getProducts().remove(product);
    }

    @Override
    public void moveCollection(String collectionToMove, String collectionToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void moveProductInCollection(Collection collection, String productToMove, String productToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<EntityAndCount<Collection>> findAllWithProductCount()
    {
        return FluentIterable.from(allPositioned()).transform(entityAndCount).toList();
    }

    public List<Collection> findAll()
    {
        return all();
    }

    public List<Collection> findAllForProduct(final Product product)
    {
        return FluentIterable.from(allPositioned()).filter(hasProduct(product)).transform(positionedToEntity).toList();
    }
}
