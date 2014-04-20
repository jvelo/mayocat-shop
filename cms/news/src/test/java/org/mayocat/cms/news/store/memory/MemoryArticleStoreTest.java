/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.store.memory;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

/**
 * Tests for {@link MemoryArticleStore}
 *
 * @version $Id$
 */
public class MemoryArticleStoreTest
{
    private ArticleStore articleStore;

    @Before
    public void setUpStore()
    {
        articleStore = new MemoryArticleStore();
    }

    @Test
    public void testFindAllPublished() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Article article1 = new Article();
        article1.setPublished(true);
        article1 = articleStore.create(article1);

        Article article2 = new Article();
        article2.setPublished(false);
        article2 = articleStore.create(article2);

        Article article3 = new Article();
        article3 = articleStore.create(article3);

        Article article4 = new Article();
        article4.setPublished(true);
        article4 = articleStore.create(article4);

        Assert.assertEquals(new Integer(2), articleStore.countAllPublished());
        Assert.assertTrue(articleStore.findAllPublished(0, 0).contains(article1));
        Assert.assertTrue(articleStore.findAllPublished(0, 0).contains(article4));
    }
}
