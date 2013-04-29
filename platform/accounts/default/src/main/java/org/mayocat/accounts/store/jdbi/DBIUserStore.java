package org.mayocat.accounts.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.mayocat.accounts.store.UserStore;
import org.mayocat.accounts.store.jdbi.dao.UserDAO;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = {"jdbi", "default"})
public class DBIUserStore extends DBIEntityStore implements UserStore, Initializable
{
    public static final String USER_TABLE_NAME = "user";

    private UserDAO dao;

    public UUID create(User user, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(user.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        user.setId(entityId);

        this.dao.createEntity(user, USER_TABLE_NAME, getTenant());
        this.dao.create(entityId, user);
        this.dao.addRoleToUser(entityId, initialRole.toString());

        this.dao.commit();

        return entityId;
    }

    public UUID create(User user) throws EntityAlreadyExistsException, InvalidEntityException
    {
        return this.create(user, Role.ADMIN);
    }

    public void update(User user, Tenant tenant) throws EntityDoesNotExistException, InvalidEntityException,
        StoreException
    {
        if (this.dao.findBySlug(user.getSlug(), tenant) != null) {
            throw new EntityDoesNotExistException();
        }
        this.dao.update(user, tenant);
    }

    public User findById(UUID id)
    {
        return this.dao.findById(id);
    }

    public List<User> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(getTenant(), number, offset);
    }

    @Override
    public List<User> findByIds(List<UUID> ids)
    {
        return this.dao.findByIds(USER_TABLE_NAME, ids);
    }

    public User findUserByEmailOrUserName(String userNameOrEmail)
    {
        return this.dao.findByEmailOrUserNameAndTenant(userNameOrEmail, getTenant());
    }

    public void update(User entity) throws InvalidEntityException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void delete(@Valid User entity) throws EntityDoesNotExistException
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(USER_TABLE_NAME, getTenant());
    }

    public List<Role> findRolesForUser(User user)
    {
        return this.dao.findRolesForUser(user);
    }
    
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(UserDAO.class);
        super.initialize();
    }


}
