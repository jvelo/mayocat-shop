/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.memory;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import org.mayocat.model.Identifiable;
import org.mayocat.model.Slug;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.Store;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

/**
 * Abstract memory store for entities that maintain a relative order in between their instances.
 *
 * @version $Id$
 */
public abstract class AbstractPositionedEntityMemoryStore<T extends Identifiable, P extends PositionedEntity<T>>
        implements Store<T, UUID>
{
    protected abstract P createForEntity(T entity, Integer position);

    protected Predicate<T> withSlug(final String slug)
    {
        return new Predicate<T>()
        {
            public boolean apply(@Nullable T input)
            {
                return ((Slug) input).getSlug().equals(slug);
            }
        };
    }

    protected Function<P, T> positionedToEntity = new Function<P, T>()
    {
        @Nullable public T apply(@Nullable P input)
        {
            return input.getEntity();
        }
    };

    private Object lock = new Object();

    private Function<P, Integer> positionOrdering = new Function<P, Integer>()
    {
        public Integer apply(@Nullable P input)
        {
            return input.getPosition();
        }
    };

    private BaseEntityMemoryStore<P> store = new BaseEntityMemoryStore<>();

    protected List<P> allPositioned()
    {
        return FluentIterable.from(Ordering.natural().onResultOf(positionOrdering)
                .sortedCopy(FluentIterable.from(store.findAll(0, 0)))).toList();
    }

    protected List<T> all()
    {
        return FluentIterable.from(Ordering.natural().onResultOf(positionOrdering)
                .sortedCopy(FluentIterable.from(store.findAll(0, 0)))).transform(
                positionedToEntity).toList();
    }

    public T findBySlug(String slug)
    {
        return FluentIterable.from(all()).filter(withSlug(slug)).first().orNull();
    }

    public T create(@Valid T entity) throws EntityAlreadyExistsException, InvalidEntityException
    {
        synchronized (lock) {
            return this.store.create(createForEntity(entity, nextPosition())).getEntity();
        }
    }

    private Integer nextPosition()
    {
        List<Integer> positions =
                FluentIterable.from(this.store.findAll(0, 0)).transform(new Function<PositionedEntity, Integer>()
                {
                    @Nullable public Integer apply(@Nullable PositionedEntity input)
                    {
                        return input.getPosition();
                    }
                }).toList();
        return FluentIterable.from(Ordering.natural().reverse().sortedCopy(positions)).first().or(-1) + 1;
    }

    public void update(@Valid T entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.store.update(forEntity(entity));
    }

    private P forEntity(T entity)
    {
        P found = this.store.findById(entity.getId());
        found.setEntity(entity);
        return found;
    }

    public void delete(@Valid T entity) throws EntityDoesNotExistException
    {
        this.store.delete(forEntity(entity));
    }

    public Integer countAll()
    {
        return store.countAll();
    }

    public List<T> findAll(Integer number, Integer offset)
    {
        return FluentIterable.from(store.findAll(number, offset)).transform(positionedToEntity).toList();
    }

    public List<T> findByIds(List<UUID> ids)
    {
        return FluentIterable.from(store.findByIds(ids)).transform(positionedToEntity).toList();
    }


    public P findPositionedById(UUID id)
    {
        return store.findById(id);
    }

    public T findById(UUID id)
    {
        return store.findById(id).getEntity();
    }
}
