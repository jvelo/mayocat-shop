/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;

import com.google.common.collect.Lists;

/**
 * Tests for {@link MemoryProductStore}
 *
 * @version $Id$
 */
public class MemoryProductStoreTest
{
    private ProductStore productStore;

    @Before
    public void setUpStore()
    {
        productStore = new MemoryProductStore();
    }

    @Test
    public void testFindBySlug() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Product product1 = new Product();
        product1.setSlug("slug-1");

        Product product2 = new Product();
        product2.setSlug("slug-2");

        Product product3 = new Product();
        product3.setSlug("slug-3");

        product1 = productStore.create(product1);
        product2 = productStore.create(product2);
        product3 = productStore.create(product3);

        Product found = productStore.findBySlug("slug-2");
        Assert.assertEquals(product2, found);
        Assert.assertNotEquals(product1, found);
        Assert.assertNotEquals(product3, found);
    }

    @Test
    public void testFindAllOnShelf() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Product product1 = new Product();
        product1.setOnShelf(true);

        Product product2 = new Product();
        Product product3 = new Product();
        product3.setOnShelf(false);

        Product product4 = new Product();
        product4.setOnShelf(true);

        productStore.create(product1);
        productStore.create(product2);
        productStore.create(product3);
        productStore.create(product4);

        List<Product> onShelf = productStore.findAllOnShelf(0, 0);
        Assert.assertEquals(2, onShelf.size());
        Assert.assertSame(product1, onShelf.get(0));
        Assert.assertSame(product4, onShelf.get(1));

        onShelf = productStore.findAllOnShelf(1, 0);
        Assert.assertEquals(1, onShelf.size());
        Assert.assertSame(product1, onShelf.get(0));

        onShelf = productStore.findAllOnShelf(1, 1);
        Assert.assertEquals(1, onShelf.size());
        Assert.assertSame(product4, onShelf.get(0));
    }

    @Test
    public void testUpdateStock()
            throws EntityDoesNotExistException, InvalidEntityException, EntityAlreadyExistsException
    {
        Product product = new Product();
        product.setOnShelf(true);
        product.setStock(100);

        product = productStore.create(product);

        productStore.updateStock(product.getId(), -20);
        Assert.assertEquals(new Integer(80), productStore.findById(product.getId()).getStock());

        productStore.updateStock(product.getId(), +50);
        Assert.assertEquals(new Integer(130), productStore.findById(product.getId()).getStock());
    }

    @Test
    public void testFindOrphans() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Collection collection1 = new Collection();
        collection1.setId(UUID.randomUUID());

        Collection collection2 = new Collection();
        collection2.setId(UUID.randomUUID());

        Product product1 = new Product();
        product1.setCollections(Arrays.asList(collection1));

        Product product2 = new Product();
        product2.setCollections(Arrays.asList(collection2));

        Product product3 = new Product();
        product3.setCollections(new ArrayList<Collection>());

        Product product4 = new Product();
        product4.setCollections(Arrays.asList(collection1, collection2));

        productStore.create(product1);
        productStore.create(product2);
        productStore.create(product3);
        productStore.create(product4);

        List<Product> orphans = productStore.findOrphanProducts();
        Assert.assertEquals(1, orphans.size());
        Assert.assertEquals(product3, orphans.get(0));
    }

    @Test
    public void testFindForCollection() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Collection collection1 = new Collection();
        collection1.setId(UUID.randomUUID());

        Collection collection2 = new Collection();
        collection2.setId(UUID.randomUUID());

        Product product1 = new Product();
        product1.setCollections(Arrays.asList(collection1));

        Product product2 = new Product();
        product2.setCollections(Arrays.asList(collection2));

        Product product3 = new Product();
        product3.setCollections(new ArrayList<Collection>());

        Product product4 = new Product();
        product4.setCollections(Arrays.asList(collection1, collection2));

        product1 = productStore.create(product1);
        product2 = productStore.create(product2);
        product3 = productStore.create(product3);
        product4 = productStore.create(product4);

        List<Product> forCollection = productStore.findAllForCollection(collection1);
        Assert.assertEquals(2, forCollection.size());
        Assert.assertEquals(product1, forCollection.get(0));
        Assert.assertEquals(product4, forCollection.get(1));
    }

    @Test
    public void testFindWithTitleLike() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Product product = new Product();
        product.setTitle("Un bel été!");

        Product product2 = new Product();
        product2.setTitle("Un bel automne!");


        productStore.create(product);

        List<Product> found = productStore.findAllWithTitleLike("été", 0, 0);
        Assert.assertEquals(1, found.size());

        found = productStore.findAllWithTitleLike("hiver", 0, 0);
        Assert.assertEquals(0, found.size());
    }

}
