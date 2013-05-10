package org.mayocat.cms.news;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.cms.news.meta.ArticleEntity;
import org.mayocat.meta.EntityMeta;
import org.mayocat.Module;

/**
 * @version $Id$
 */
public class NewsModule implements Module
{
    private static final List<EntityMeta> entities = new ArrayList<EntityMeta>();

    static {
        entities.add(new ArticleEntity());
    }

    @Override
    public String getName()
    {
        return "news";
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
