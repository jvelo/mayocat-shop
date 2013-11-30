/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.cms.news.model.Article;

/**
 * @version $Id$
 */
public class ArticleEntity implements EntityMeta
{
    public static final String ID = "article";

    public static final String PATH = "news";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return Article.class;
    }
}
