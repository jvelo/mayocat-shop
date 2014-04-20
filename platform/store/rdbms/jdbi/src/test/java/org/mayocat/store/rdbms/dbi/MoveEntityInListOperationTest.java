/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.mayocat.model.Entity;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidMoveOperation;

/**
 * @version $Id$
 */
public class MoveEntityInListOperationTest
{
    private class ExampleEntity implements Entity
    {
        private String slug;

        private UUID id;

        private ExampleEntity(String slug, UUID id)
        {
            this.slug = slug;
            this.id = id;
        }

        public String getSlug()
        {
            return slug;
        }

        public void setSlug(String slug)
        {
            this.slug = slug;
        }

        public UUID getId()
        {
            return id;
        }

        public void setId(UUID id)
        {
            this.id = id;
        }
    }

    @Test
    public void testMove() throws Exception
    {
        List<ExampleEntity> entities = new LinkedList<ExampleEntity>();
        entities.add(new ExampleEntity("first", UUID.randomUUID()));
        entities.add(new ExampleEntity("second", UUID.randomUUID()));

        MoveEntityInListOperation<ExampleEntity>  moveOp =
        new MoveEntityInListOperation<ExampleEntity>(entities, "first", "second",
                HasOrderedCollections.RelativePosition.AFTER);

        Assert.assertTrue(moveOp.hasMoved());
    }
}
