package org.mayocat.meta;

import java.util.List;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityMetaRegistry
{
    List<EntityMeta> getEntities();
}
