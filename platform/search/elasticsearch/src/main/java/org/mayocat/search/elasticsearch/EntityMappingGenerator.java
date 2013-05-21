package org.mayocat.search.elasticsearch;

import java.util.Map;

import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * Entity mapping generators do provide elastic search mappings (in the sense of http://www.elasticsearch.org/guide/reference/mapping/)
 * for a certain type of mayocat entity.
 *
 * @version $Id$
 */
@Role
public interface EntityMappingGenerator
{
    /**
     * @return the (entity) class covered by this mapping generator
     */
    Class forClass();

    /**
     * @return the elastic search mapping for this mayocat entity, as a Map.
     */
    Map<String, Object> generateMapping();
}
