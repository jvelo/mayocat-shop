/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import java.net.URL;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * Use this component to create URL for entities.
 *
 * @version $Id$
 */
@Role
public interface EntityURLFactory<E extends Entity>
{
    /**
     * Creates the URL of a certain type (API or public) for a given entity, belonging to a given tenant
     *
     * @param entity the entity to create the URL for
     * @param tenant the tenant the entity belongs to
     * @param type the type of URL to create. For example public, or API URL
     * @return the URL corresponding to this entity and tenant
     */
    URL create(E entity, Tenant tenant, URLType type);

    /**
     * Same as {@link #create(org.mayocat.model.Entity, org.mayocat.accounts.model.Tenant, URLType)} with the {@link
     * URLType#PUBLIC} type of URL.
     *
     * @param entity the entity to create the URL for
     * @param tenant the tenant the entity belongs to
     * @return the public URL corresponding to this entity and tenant
     */
    URL create(E entity, Tenant tenant);

    /**
     * Same as {@link #create(org.mayocat.model.Entity, org.mayocat.accounts.model.Tenant, URLType)} with the {@link
     * URLType#PUBLIC} type of URL, and the context's tenant (the tenant this request is executed against)
     *
     * @param entity the entity to create the URL for
     * @return the public URL corresponding to this entity and tenant
     */
    URL create(E entity);
}
