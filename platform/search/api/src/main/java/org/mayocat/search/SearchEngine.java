package org.mayocat.search;

import java.util.List;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

@Role
public interface SearchEngine
{
    void index(Entity entity, Tenant tenant) throws SearchEngineException;

    void index(Entity entity) throws SearchEngineException;

    List<Map<String, Object>> search(String term, List<Class<? extends Entity>> entityTypes)
            throws SearchEngineException;
}
