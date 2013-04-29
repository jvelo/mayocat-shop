package org.mayocat.model;

import java.util.UUID;

/**
 * @version $Id$
 */
public interface Child
{
    UUID getParentId();

    void setParentId(UUID id);
}
