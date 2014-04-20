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
 *
 * Represents a pair of an entity and a count associated with it.
 */
public class EntityAndCount<E>
{
    private E entity;

    private Long count;

    public EntityAndCount(E e, Long c)
    {
        this.entity = e;
        this.count = c;
    }

    public E getEntity()
    {
        return entity;
    }

    public Long getCount()
    {
        return count;
    }
}
