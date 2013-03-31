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
