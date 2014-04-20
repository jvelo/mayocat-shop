/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.store.memory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

/**
 * Tests for {@link MemoryPageStore}.
 *
 * @version $Id$
 */
public class MemoryPageStoreTest
{
    private PageStore pageStore;

    @Before
    public void setUpStore()
    {
        this.pageStore = new MemoryPageStore();
    }

    @Test
    public void testFindRootPages() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Page page1 = pageStore.create(new Page());

        Page page2 = new Page();
        page2.setParentId(page1.getId());
        page2 = pageStore.create(page2);

        Page page3 = new Page();
        page3.setParentId(page1.getId());
        page3 = pageStore.create(page3);

        Page page4 = pageStore.create(new Page());

        Assert.assertEquals(2, pageStore.findAllRootPages().size());
        Assert.assertTrue(pageStore.findAllRootPages().contains(page1));
        Assert.assertTrue(pageStore.findAllRootPages().contains(page4));
    }
}
