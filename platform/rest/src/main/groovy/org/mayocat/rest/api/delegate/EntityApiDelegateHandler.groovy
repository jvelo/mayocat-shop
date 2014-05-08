/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.delegate

import groovy.transform.CompileStatic
import org.mayocat.model.Entity

/**
 * Helper interface to abstract the entity stores/repository from the API delegates
 *
 * @version $Id$
 */
@CompileStatic
interface EntityApiDelegateHandler
{
    /**
     * Retrieve an entity by slug against the persistence storage.
     *
     * @param slug the slug of the entity to retrieve
     * @return the retrieved entity
     */
    Entity getEntity(String slug)

    /**
     * Updates the entity against the persistence storage
     *
     * @param entity the entity to update
     */
    void updateEntity(Entity entity)

    /**
     * @return the type of entity, for example "product" or "user" etc.
     */
    String type();
}
