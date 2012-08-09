package org.mayocat.shop.search;

import org.mayocat.shop.model.Entity;
import org.xwiki.component.annotation.Role;

@Role
public interface SearchEngine
{
    void index(Entity t) throws SearchEngineException;
}
