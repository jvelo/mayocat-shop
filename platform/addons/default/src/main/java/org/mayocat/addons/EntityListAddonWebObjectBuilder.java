package org.mayocat.addons;

import org.mayocat.entity.EntityData;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityListAddonWebObjectBuilder
{
    Object build(EntityData<Entity> entity);
}
