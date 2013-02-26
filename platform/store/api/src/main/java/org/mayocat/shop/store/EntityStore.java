package org.mayocat.shop.store;

import java.io.Serializable;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.reference.EntityReference;

/**
 * @version $Id$
 */
public interface EntityStore
{
    EntityReference getReference(Long id);

    Long getId(EntityReference reference);
}
