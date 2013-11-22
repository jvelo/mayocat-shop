package org.mayocat.store.memory;

import java.util.UUID;

import org.mayocat.model.Identifiable;

/**
 * Doc goes here.
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
