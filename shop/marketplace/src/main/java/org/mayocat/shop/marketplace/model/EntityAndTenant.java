/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.model;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;

/**
 * @version $Id$
 */
public class EntityAndTenant<E extends Entity>
{
    private final E entity;

    private final Tenant tenant;

    public EntityAndTenant(E entity, Tenant tenant)
    {
        this.entity = entity;
        this.tenant = tenant;
    }

    public Tenant getTenant()
    {
        return this.tenant;
    }

    public E getEntity()
    {
        return this.entity;
    }
}
