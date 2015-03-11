/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.Module;
import org.mayocat.meta.EntityMeta;
import org.mayocat.shop.customer.meta.CustomerEntity;

/**
 * @version $Id$
 */
public class CustomerModule implements Module
{
    private static final List<EntityMeta> entities = new ArrayList<EntityMeta>();

    static {
        entities.add(new CustomerEntity());
    }

    @Override
    public String getName()
    {
        return "customer";
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
