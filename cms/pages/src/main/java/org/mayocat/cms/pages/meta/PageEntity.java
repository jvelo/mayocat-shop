/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
