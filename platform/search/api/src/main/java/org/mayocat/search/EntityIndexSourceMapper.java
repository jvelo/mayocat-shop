package org.mayocat.search;

import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityIndexSourceMapper
{
    Class forClass();

    Map<String, Object> mapSource(Entity entity);

    Map<String, Object> mapSource(Entity entity, Tenant tenant);
}
