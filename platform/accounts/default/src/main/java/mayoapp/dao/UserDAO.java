/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.store.jdbi.mapper.RoleMapper;
import org.mayocat.accounts.store.jdbi.mapper.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@RegisterMapper(UserMapper.class)
@UseStringTemplate3StatementLocator
public abstract class UserDAO implements EntityDAO<User>, Transactional<UserDAO>
{
    private static final String USER_TABLE_NAME = "agent";

    @SqlUpdate
    public abstract void create(@BindBean("user") User user);

    @SqlUpdate
    public abstract void addRoleToUser(@Bind("userId") UUID userId, @Bind("role") String role);

    @SqlUpdate
    public abstract void update(@BindBean("u") User user, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract User findByEmailOrUserNameAndTenant(@Bind("userNameOrEmail") String userNameOrEmail,
            @BindBean("t") Tenant tenant);

    @SqlQuery
    public abstract User findGlobalUserByEmailOrUserName(@Bind("userNameOrEmail") String userNameOrEmail);

    @RegisterMapper(RoleMapper.class)
    @SqlQuery
    public abstract List<Role> findRolesForUser(@BindBean("user") User user);

    @SqlQuery
    public abstract List<User> findAllUsers(@BindBean("tenant") Tenant tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract List<User> findAllGlobalUsers(@Bind("number") Integer number, @Bind("offset") Integer offset);

    @SqlQuery
    protected abstract User findUserBySlug(@Bind("slug") String slug, @BindBean("tenant") Tenant tenant);

    public User findById(UUID id)
    {
        return this.findById(USER_TABLE_NAME, id);
    }

    public User findBySlug(String slug, Tenant tenant)
    {
        return this.findUserBySlug(slug, tenant);
    }

    public List<User> findAll(Tenant tenant, Integer number, Integer offset)
    {
        return this.findAllUsers(tenant, number, offset);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Integer countAll(@Define("type") String type, @BindBean("tenant") Tenant tenant)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @BindBean("tenant") Tenant tenant,
            @Bind("number") Integer number, @Bind("offset") Integer offset)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @Define("order") String order,
            @BindBean("tenant") Tenant tenant)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @BindBean("tenant") Tenant tenant)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public User findBySlug(@Define("type") String type, @Bind("slug") String slug)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @Define("order") String order,
            @BindBean("tenant") Tenant tenant, @Bind("number") Integer number, @Bind("offset") Integer offset)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }
}