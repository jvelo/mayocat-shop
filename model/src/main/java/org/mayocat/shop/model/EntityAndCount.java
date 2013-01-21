package org.mayocat.shop.model;

/**
 * @version $Id$
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
