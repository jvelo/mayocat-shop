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
    public abstract void update(@BindBean("u") User user);

    @SqlUpdate
    public abstract void changePassword(@BindBean("user") User user, @Bind("hash") String hash);

    @SqlUpdate
    public abstract void createPasswordResetRequest(@BindBean("user") User user, @Bind("resetKey") String resetKey);

    // TODO try and register a generic UUID mapper that can extract a UUID from the first result like StringMapper etc.
    @SqlQuery
    public abstract String findUserIdForPasswordResetKey(@Bind("resetKey") String resetKey);

    @SqlUpdate
    public abstract void deletePasswordResetRequest(@Bind("resetKey") String resetKey);

    @SqlUpdate
    public abstract void updateGlobalUser(@BindBean("user") User user);

    @SqlQuery
    public abstract User findByEmailOrUserNameAndTenant(@Bind("userNameOrEmail") String userNameOrEmail,
            @Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract User findGlobalUserByEmailOrUserName(@Bind("userNameOrEmail") String userNameOrEmail);

    @RegisterMapper(RoleMapper.class)
    @SqlQuery
    public abstract List<Role> findRolesForUser(@BindBean("user") User user);

    @SqlQuery
    public abstract List<User> findAllUsers(@Bind("tenantId") UUID tenantId, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract List<User> findAllGlobalUsers(@Bind("number") Integer number, @Bind("offset") Integer offset);

    @SqlQuery
    public abstract User findByValidationKey(@Bind("validationKey") String validationKey);

    @SqlQuery
    protected abstract User findUserBySlug(@Bind("slug") String slug, @Bind("tenantId") UUID tenant);

    public User findById(UUID id)
    {
        return this.findById(USER_TABLE_NAME, id);
    }

    public User findBySlug(String slug, UUID tenant)
    {
        return this.findUserBySlug(slug, tenant);
    }

    public List<User> findAll(UUID tenantId, Integer number, Integer offset)
    {
        return this.findAllUsers(tenantId, number, offset);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Integer countAll(@Define("type") String type, @Bind("tenantId") UUID tenantId)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @Bind("tenantId") UUID tenantId,
            @Bind("number") Integer number, @Bind("offset") Integer offset)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @Define("order") String order,
            @Bind("tenantId") UUID tenantId)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<User> findAll(@Define("type") String type, @Bind("tenantId") UUID tenantId)
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
            @Bind("tenantId") UUID tenantId, @Bind("number") Integer number, @Bind("offset") Integer offset)
    {
        // Make sure nobody uses the generic Entity DAO version since it does not work for the user case
        // where the table name (agent) is different than the entity type name (user)
        throw new RuntimeException("Not implemented.");
    }
}