package org.mayocat.entity;

import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityLoader
{
    <E extends Entity> E load(String slug);

    <E extends Entity> E load(String slug, String tenantSlug);
}
