package org.mayocat.cms.pages;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.meta.EntityMeta;
import org.mayocat.Module;
import org.mayocat.cms.pages.meta.PageEntity;

/**
 * @version $Id$
 */
public class PagesModule implements Module
{
    private static final List<EntityMeta> entities = new ArrayList<EntityMeta>();

    static {
        entities.add(new PageEntity());
    }

    @Override
    public String getName()
    {
        return "pages";
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
