/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.model;

/**
 * @version $Id$
 */
public class PositionedEntity<E extends Entity>
{
    private E entity;

    private Integer position;

    public PositionedEntity(E entity, Integer position)
    {
        this.entity = entity;
        this.position = position;
    }

    public E getEntity()
    {
        return entity;
    }

    public Integer getPosition()
    {
        return position;
    }
}
