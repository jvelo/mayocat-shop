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
