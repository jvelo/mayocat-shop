package org.mayocat.search.elasticsearch;

import java.util.Map;

import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntitySourceExtractor
{
    Map<String, Object> extract(Entity entity);
}
