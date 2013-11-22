package org.mayocat.store.memory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import org.mayocat.model.Child;
import org.mayocat.model.Entity;
import org.mayocat.model.Identifiable;
import org.mayocat.model.Slug;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.Store;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class BaseEntityMemoryStore<T extends Identifiable> implements Store<T, UUID>
{
    protected Map<UUID, T> entities = Maps.newConcurrentMap();

    protected Predicate<T> withSlug(final String slug)
    {
        return new Predicate<T>()
        {
            @Override public boolean apply(@Nullable T input)
            {
                if (!Slug.class.isAssignableFrom(input.getClass())) {
                    throw new RuntimeException("Cannot apply by slug filter to non-slug entity");
                }

                return ((Slug) input).getSlug().equals(slug);
            }
        };
    }

    protected Predicate<T> withParent(final Entity parent)
    {
        return new Predicate<T>()
        {
            @Override public boolean apply(@Nullable T input)
            {
                if (!Child.class.isAssignableFrom(input.getClass())) {
                    throw new RuntimeException("Cannot apply with parent filter to non-child");
                }

                return parent.getId().equals(((Child) input).getParentId());
            }
        };
    }

    protected Predicate<T> withParentId(final UUID... parentId)
    {
        return new Predicate<T>()
        {
            @Override public boolean apply(@Nullable T input)
            {
                if (!Child.class.isAssignableFrom(input.getClass())) {
                    throw new RuntimeException("Cannot apply with parent filter to non-child");
                }

                return Arrays.asList(parentId).contains(((Child) input).getParentId());
            }
        };
    }

    @Override
    public T create(@Valid T entity) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        if (exists(entity)) {
            throw new EntityAlreadyExistsException();
        }

        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void update(@Valid T entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        if (!exists(entity)) {
            throw new EntityDoesNotExistException();
        }

        entities.put(entity.getId(), entity);
    }

    @Override
    public void delete(@Valid T entity) throws EntityDoesNotExistException
    {
        if (!exists(entity)) {
            throw new EntityDoesNotExistException();
        }

        entities.remove(entity.getId());
    }

    @Override
    public Integer countAll()
    {
        return entities.size();
    }

    @Override
    public List<T> findAll(Integer number, Integer offset)
    {
        return FluentIterable.from(entities.values()).skip(offset).limit(number).toList();
    }

    @Override
    public List<T> findByIds(final List<UUID> ids)
    {
        return FluentIterable.from(entities.values()).filter(new Predicate<T>()
        {
            @Override public boolean apply(@Nullable T input)
            {
                return ids.contains(input);
            }
        }).toList();
    }

    @Override
    public T findById(UUID id)
    {
        return entities.get(id);
    }

    private boolean exists(T entity)
    {
        return entity.getId() != null && entities.containsKey(entity.getId());
    }
}
