package org.mayocat.shop.search;

import java.util.List;
import java.util.Map;

import org.mayocat.shop.model.EntityWithSlug;
import org.xwiki.component.annotation.Role;

@Role
public interface SearchEngine
{
    void index(EntityWithSlug t) throws SearchEngineException;

    List<Map<String, Object>> search(String term, List<Class< ? extends EntityWithSlug>> entityTypes) throws SearchEngineException;
}
