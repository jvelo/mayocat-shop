package org.mayocat.accounts.store.jdbi;

import java.util.List;

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
    //@Inject
    //private DBIProvider dbi;

    private UserDAO dao;

    public void create(User user, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(user.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        this.dao.createEntity(user, "user", getTenant());
        Long entityId = this.dao.getId(user, "user", getTenant());
        this.dao.create(entityId, user);
        this.dao.addRoleToUser(entityId, initialRole.toString());

        this.dao.commit();
    }

    public void create(User user) throws EntityAlreadyExistsException, InvalidEntityException
    {
        this.create(user, Role.ADMIN);
    }

    public void update(User user, Tenant tenant) throws EntityDoesNotExistException, InvalidEntityException,
        StoreException
    {
        if (this.dao.findBySlug(user.getSlug(), tenant) != null) {
            throw new EntityDoesNotExistException();
        }
        this.dao.update(user, tenant);
    }

    public User findById(Long id)
    {
        return this.dao.findById(id);
    }

    public List<User> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(getTenant(), number, offset);
    }

    public User findUserByEmailOrUserName(String userNameOrEmail)
    {
        return this.dao.findByEmailOrUserNameAndTenant(userNameOrEmail, getTenant());
    }

    public void update(User entity) throws InvalidEntityException
    {
        // TODO Auto-generated method stub
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
