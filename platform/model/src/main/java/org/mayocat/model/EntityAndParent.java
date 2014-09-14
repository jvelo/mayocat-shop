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
