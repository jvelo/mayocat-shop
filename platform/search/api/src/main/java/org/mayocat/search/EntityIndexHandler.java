package org.mayocat.search;

import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityIndexHandler
{
    Class forClass();

    Map<String, Object> getDocument(Entity entity);

    Map<String, Object> getDocument(Entity entity, Tenant tenant);

    void updateMapping();
}
