/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.internal.meta;

import java.util.List;

import org.mayocat.meta.EntityMeta;
import org.mayocat.meta.EntityMetaRegistry;

/**
 * @version $Id$
 */
public class DefaultEntityMetaRegistry implements EntityMetaRegistry
{
    private List<EntityMeta> entities;

    public DefaultEntityMetaRegistry(List<EntityMeta> entities)
    {
        this.entities = entities;
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
