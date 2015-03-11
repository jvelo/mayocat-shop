/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.mapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mayocat.model.EntityList;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * Mapper for {@link EntityList}
 *
 * @version $Id$
 */
public class EntityListMapper implements ResultSetMapper<EntityList>
{
    public EntityList map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        EntityList list = new EntityList();

        list.setId((UUID) resultSet.getObject("id"));
        list.setParentId((UUID) resultSet.getObject("parent_id"));
        list.setSlug(resultSet.getString("slug"));
        list.setHint(resultSet.getString("hint"));
        list.setType(resultSet.getString("entity_type"));

        if (resultSet.getArray("entities") != null) {
            // There's no support for getting the pg uuid array as a Java UUID array (or even String array) at the time
            // this is written, we have to iterate over the array own result set and construct the Java array ourselves
            List<UUID> ids = new ArrayList<>();
            Array array = resultSet.getArray("entities");
            if (array != null) {
                ResultSet entitiesResultSet = array.getResultSet();
                while (entitiesResultSet.next()) {
                    ids.add((UUID) entitiesResultSet.getObject("value"));
                }
                list.setEntities(ids);
            }
        }

        return list;
    }
}
