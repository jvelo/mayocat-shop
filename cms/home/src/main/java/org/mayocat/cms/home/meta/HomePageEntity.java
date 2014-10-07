/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.home.meta;

import org.mayocat.cms.home.model.HomePage;
import org.mayocat.meta.EntityMeta;

/**
 * @version $Id$
 */
public class HomePageEntity implements EntityMeta
{
    public static final String ID = "home";

    public String getEntityName()
    {
        return ID;
    }

    public Class getEntityClass()
    {
        return HomePage.class;
    }
}
