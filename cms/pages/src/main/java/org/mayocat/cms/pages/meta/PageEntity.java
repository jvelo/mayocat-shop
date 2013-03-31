package org.mayocat.cms.pages.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.cms.pages.model.Page;

/**
 * @version $Id$
 */
public class PageEntity implements EntityMeta
{
    public static final String ID = "page";

    public static final String PATH = "pages";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return Page.class;
    }
}
