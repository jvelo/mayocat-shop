/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
