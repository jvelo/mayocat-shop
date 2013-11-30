/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.cms.news.model.Article;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ArticleStore extends Store<Article, UUID>, EntityStore
{
    Article findBySlug(String slug);

    List<Article> findAllPublished(Integer offset, Integer number);

    Integer countAllPublished();
}
