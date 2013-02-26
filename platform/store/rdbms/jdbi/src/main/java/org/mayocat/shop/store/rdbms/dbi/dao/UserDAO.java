package org.mayocat.shop.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.rdbms.dbi.mapper.RoleMapper;
import org.mayocat.shop.store.rdbms.dbi.mapper.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@RegisterMapper(UserMapper.class)
@UseStringTemplate3StatementLocator
public abstract class UserDAO implements EntityDAO<User>, Transactional<UserDAO>
{
    private static final String USER_TABLE_NAME = "agent";

    @SqlUpdate
    (
        "INSERT INTO agent (entity_id, email, password) VALUES (:id, :user.email, :user.password)"
    )
    public abstract void create(@Bind("id") Long entityId, @BindBean("user") User user);

    @SqlUpdate
    (
        "INSERT INTO agent_role (agent_id, role) VALUES (:userId, :role)"
    )
    public abstract void addRoleToUser(@Bind("userId") Long userId, @Bind("role") String role);
    
    @SqlUpdate
    (
        "UPDATE agent SET email=:u.email, password=:u.password WHERE id=:u.id"
    )
    public abstract void update(@BindBean("u") User user, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT * FROM entity INNER JOIN agent ON entity.id=agent.entity_id WHERE " +
        "(agent.email=:userNameOrEmail OR entity.slug=:userNameOrEmail) AND entity.tenant_id=:t.id AND entity.type='user'"
    )
    public abstract User findByEmailOrUserNameAndTenant(@Bind("userNameOrEmail") String userNameOrEmail, @BindBean("t") Tenant tenant);

    @RegisterMapper(RoleMapper.class)
    @SqlQuery
    (
        "SELECT * FROM agent_role WHERE agent_id = :user.id"
    )
    public abstract List<Role> findRolesForUser(@BindBean("user") User user);

    
    public User findById(Long id)
    {
        return this.findById(USER_TABLE_NAME, id);
    }
    
    public User findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlug(USER_TABLE_NAME, slug, tenant);
    }

    public List<User> findAll(Tenant tenant, Integer number, Integer offset)
    {
        return this.findAll(USER_TABLE_NAME, tenant, number, offset);
    }

}
