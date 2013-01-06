package org.mayocat.shop.search;

import java.util.List;
import java.util.Map;

import org.mayocat.shop.model.Entity;
import org.xwiki.component.annotation.Role;

@Role
public interface SearchEngine
{
    void index(Entity t) throws SearchEngineException;

    List<Map<String, Object>> search(String term, List<Class< ? extends Entity>> entityTypes) throws SearchEngineException;
}
