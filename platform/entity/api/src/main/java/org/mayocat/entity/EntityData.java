/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.entity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mayocat.model.Entity;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class EntityData<T extends Entity>
{
    private T entity;

    private Optional<Entity> parent;

    private Map<Class<? extends Entity>, List<Entity>> children = Maps.newHashMap();

    private Map<Class<?>, Object> data = Maps.newHashMap();

    public EntityData(T entity)
    {
        this.entity = entity;
    }

    public T getEntity()
    {
        return entity;
    }

    public Optional<Entity> getParent()
    {
        return parent;
    }

    public <E extends Entity> List<E> getChildren(Class<E> clazz)
    {
        if (children.containsKey(clazz)) {
            return (List<E>) children.get(clazz);
        }
        return Collections.emptyList();
    }

    public Set<Class<? extends Entity>> getChildrenTypes()
    {
        return children.keySet();
    }

    public <O> List<O> getDataList(Class<O> clazz)
    {
        if (data.containsKey(clazz)) {
            return (List<O>) data.get(clazz);
        }
        return Collections.emptyList();
    }

    public <O> Optional<O> getData(Class<O> clazz)
    {
        if (data.containsKey(clazz)) {
            return Optional.of((O) data.get(clazz));
        }
        return Optional.absent();
    }

    public Set<Class<?>> getDataTypes()
    {
        return data.keySet();
    }


    public void setParent(Entity e)
    {
        parent = Optional.of(e);
    }

    public <E extends Entity> void setChildren(Class<E> clazz, List<E> list)
    {
        children.put(clazz, (List<Entity>) list);
    }

    public <O> void setData(Class<O> clazz, O object)
    {
        data.put(clazz, object);
    }

    public <O> void setDataList(Class<O> clazz, List<O> object)
    {
        data.put(clazz, object);
    }
}
