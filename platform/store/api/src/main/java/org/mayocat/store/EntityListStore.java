/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.model.EntityList;
import org.xwiki.component.annotation.Role;

/**
 * Store interface for {@link EntityList}
 *
 * @version $Id$
 */
@Role
public interface EntityListStore extends Store<EntityList, UUID>, EntityStore
{
    EntityList getOrCreate(EntityList entityList) throws InvalidEntityException;

    List<EntityList> findListsByHint(String hint);

    EntityList findListByHintAndParentId(String hint, UUID parentId);

    void addEntityToList(EntityList list, UUID entity) throws EntityDoesNotExistException;

    void removeEntityFromList(EntityList list, UUID entity) throws EntityDoesNotExistException;
}
