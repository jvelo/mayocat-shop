/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.memory;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

/**
 * Tests for {@link MemoryCollectionStore}
 *
 * @version $Id$
 */
public class MemoryCollectionStoreTest
{
    private CollectionStore collectionStore;

    @Before
    public void setUpStore()
    {
        collectionStore = new MemoryCollectionStore();
    }

    @Test
    public void testFindForProduct() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Product product1 = new Product(UUID.randomUUID());
        Product product2 = new Product(UUID.randomUUID());

        Collection collection1 = collectionStore.create(new Collection());
        Collection collection2 = collectionStore.create(new Collection());
        Collection collection3 = collectionStore.create(new Collection());
        Collection collection4 = collectionStore.create(new Collection());

        collectionStore.addProduct(collection1, product1);
        collectionStore.addProduct(collection3, product1);
        collectionStore.addProduct(collection2, product2);

        List<Collection> forProduct = collectionStore.findAllForProduct(product1);

        Assert.assertEquals(2, forProduct.size());
        Assert.assertEquals(collection1, forProduct.get(0));
        Assert.assertEquals(collection3, forProduct.get(1));
    }

    @Test
    public void testFindAllWithProductCount() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Product product1 = new Product(UUID.randomUUID());
        Product product2 = new Product(UUID.randomUUID());

        Collection collection1 = collectionStore.create(new Collection());
        Collection collection2 = collectionStore.create(new Collection());
        Collection collection3 = collectionStore.create(new Collection());

        collectionStore.addProduct(collection1, product1);
        collectionStore.addProduct(collection1, product2);
        collectionStore.addProduct(collection2, product2);

        List<EntityAndCount<Collection>> allWithProductsCount = collectionStore.findAllWithProductCount();

        Assert.assertEquals(3, allWithProductsCount.size());

        Assert.assertEquals(new Long(2), allWithProductsCount.get(0).getCount());
        Assert.assertEquals(collection1, allWithProductsCount.get(0).getEntity());

        Assert.assertEquals(new Long(1), allWithProductsCount.get(1).getCount());
        Assert.assertEquals(collection2, allWithProductsCount.get(1).getEntity());

        Assert.assertEquals(new Long(0), allWithProductsCount.get(2).getCount());
        Assert.assertEquals(collection3, allWithProductsCount.get(2).getEntity());
    }
}
