package org.mayocat;

import java.util.List;

import org.mayocat.meta.EntityMeta;

/**
 * @version $Id$
 */
public interface Module
{
    String getName();

    List<EntityMeta> getEntities();
}
