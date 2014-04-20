/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
