package org.mayocat.cms.news;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.meta.EntityMeta;
import org.mayocat.Module;
import org.mayocat.cms.news.meta.ArticleMeta;

/**
 * @version $Id$
 */
public class NewsModule implements Module
{
    private static final List<EntityMeta> entities = new ArrayList<EntityMeta>();

    static {
        entities.add(new ArticleMeta());
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
