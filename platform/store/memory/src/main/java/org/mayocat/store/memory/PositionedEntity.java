package org.mayocat.store.memory;

import org.mayocat.model.Identifiable;

/**
 * Doc goes here.
 *
 * @version $Id$
 */
public interface PositionedEntity<T> extends Identifiable
{
    Integer getPosition();

    T getEntity();

    void setEntity(T entity);
}
