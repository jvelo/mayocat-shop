package org.mayocat.cms.news.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.cms.news.model.Article;

/**
 * @version $Id$
 */
public class ArticleMeta implements EntityMeta
{
    @Override
    public String getEntityName()
    {
        return "article";
    }

    @Override
    public Class getEntityClass()
    {
        return Article.class;
    }
}
