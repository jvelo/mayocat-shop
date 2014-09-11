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
