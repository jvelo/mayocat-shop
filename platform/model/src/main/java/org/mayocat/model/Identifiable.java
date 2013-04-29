package org.mayocat.model;

import java.util.UUID;

/**
 * @version $Id$
 */
public interface Identifiable
{
    UUID getId();

    void setId(UUID id);
}
