/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.store.memory;

import java.util.List;

import javax.annotation.Nullable;

import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.store.memory.BaseEntityMemoryStore;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link ArticleStore}
 *
 * @version $Id$
 */
public class MemoryArticleStore extends BaseEntityMemoryStore<Article> implements ArticleStore
{
    public Article findBySlug(String slug)
    {
        return FluentIterable.from(all()).filter(withSlug(slug)).first().orNull();
    }

    @Override
    public List<Article> findAllPublished(Integer offset, Integer number)
    {
        Iterable<Article> allPublished = FluentIterable.from(all()).filter(new Predicate<Article>()
        {
            public boolean apply(@Nullable Article input)
            {
                return input.getPublished() != null && input.getPublished();
            }
        });

        if (number == 0) {
            return FluentIterable.from(allPublished).skip(offset).toList();
        }
        return FluentIterable.from(allPublished).skip(offset).limit(number).toList();
    }

    @Override
    public Integer countAllPublished()
    {
        return findAllPublished(0, 0).size();
    }
}
