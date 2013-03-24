package org.mayocat.cms.pages.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.cms.pages.model.Page;

/**
 * @version $Id$
 */
public class PageEntity implements EntityMeta
{
    @Override
    public String getEntityName()
    {
        return "page";
    }

    @Override
    public Class getEntityClass()
    {
        return Page.class;
    }
}
