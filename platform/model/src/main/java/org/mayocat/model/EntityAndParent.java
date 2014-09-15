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
public class EntityAndParent<E extends Entity>
{
    private final EntityAndParent<E> parent;

    private final E entity;

    public EntityAndParent(EntityAndParent<E> parent, E entity)
    {
        this.parent = parent;
        this.entity = entity;
    }

    public EntityAndParent<E> getParent()
    {
        return parent;
    }

    public E getEntity()
    {
        return entity;
    }
}
