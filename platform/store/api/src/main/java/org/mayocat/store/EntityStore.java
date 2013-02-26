package org.mayocat.store;

import org.mayocat.model.reference.EntityReference;

/**
 * @version $Id$
 */
public interface EntityStore
{
    EntityReference getReference(Long id);

    Long getId(EntityReference reference);
}
