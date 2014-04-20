/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.memory;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.model.Identifiable;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class BaseEntityMemoryStoreTest
{
    private static class MyEntity implements Identifiable
    {
        private UUID uuid;

        private MyEntity()
        {
        }

        private MyEntity(UUID uuid)
        {
            this.uuid = uuid;
        }

        @Override
        public UUID getId()
        {
            return uuid;
        }

        @Override
        public void setId(UUID id)
        {
            this.uuid = id;
        }
    }

    private BaseEntityMemoryStore<MyEntity> store;

    @Before
    public void setUpStore()
    {
        store = new BaseEntityMemoryStore();
    }

    @Test
    public void testCreateThenFindById() throws Exception
    {
        MyEntity entity = store.create(new MyEntity());
        Assert.assertSame(entity, store.findById(entity.getId()));
    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void testCreateSeveralTimesWithSameIdThrowsException() throws Exception
    {
        MyEntity entity = store.create(new MyEntity());
        store.create(entity);
    }


    @Test(expected = EntityDoesNotExistException.class)
    public void testUpdateEntityWithNoIdThrowsException() throws Exception
    {
        store.update(new MyEntity());
    }

    @Test(expected = EntityDoesNotExistException.class)
    public void testUpdateEntityThatDoesNotExistsThrowsException() throws Exception
    {
        store.update(new MyEntity(UUID.randomUUID()));
    }

    @Test(expected = EntityDoesNotExistException.class)
    public void testDeleteEntityWithNoIdThrowsException() throws Exception
    {
        store.delete(new MyEntity());
    }

    @Test(expected = EntityDoesNotExistException.class)
    public void testDeleteEntityThatDoesNotExistsThrowsException() throws Exception
    {
        store.delete(new MyEntity(UUID.randomUUID()));
    }

    @Test
    public void testCountAll() throws Exception
    {
        for (int i = 0; i < 15; i++) {
            store.create(new MyEntity());
        }

        Assert.assertEquals(new Integer(15), store.countAll());
    }

    @Test
    public void testFindAll() throws Exception
    {
        List<MyEntity> created = Lists.newArrayList();
        for (int i = 0; i < 15; i++) {
            created.add(store.create(new MyEntity()));
        }

        List<MyEntity> all = store.findAll(10, 0);
        Assert.assertEquals(10, all.size());

        for (MyEntity entity : all) {
            Assert.assertTrue(created.contains(entity));
        }
    }
}
