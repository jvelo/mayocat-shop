package org.mayocat.shop.model;

import org.mayocat.shop.model.reference.EntityReference;

/**
 * @version $Id$
 */
public interface Child
{
    Long getParentId();

    void setParentId(Long id);
}
