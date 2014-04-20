/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.memory;

import java.util.UUID;

import org.mayocat.model.Identifiable;

/**
 * Default positioned entity for memory stores.
 *
 * @version $Id$
 */
public class DefaultPositionedEntity<T extends Identifiable> implements PositionedEntity<T>
{
    private T entity;

    private Integer position;

    public DefaultPositionedEntity(T entity, Integer position)
    {
        this.entity = entity;
        this.position = position;
    }

    public UUID getId()
    {
        return entity.getId();
    }

    public void setId(UUID id)
    {
        entity.setId(id);
    }

    public void setEntity(T entity)
    {
        this.entity = entity;
    }

    public Integer getPosition()
    {
        return position;
    }

    public T getEntity()
    {
        return entity;
    }
}
