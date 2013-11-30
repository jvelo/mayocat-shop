/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.accounts.model.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class UserMapper implements ResultSetMapper<User>
{
    @Override
    public User map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        User user = new User((UUID) result.getObject("id"));
        user.setEmail(result.getString("email"));
        user.setPassword(result.getString("password"));
        user.setSlug(result.getString("slug"));

        if (result.getObject("tenant_id") == null) {
            user.setGlobal(true);
        }

        return user;
    }
}
