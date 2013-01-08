package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.mayocat.shop.store.rdbms.dbi.dao.UserDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = {"jdbi", "default"})
public class DBIUserStore extends AbstractEntityStore implements UserStore, Initializable
{
    @Inject
    private DBIProvider dbi;

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
        this.dao = this.dbi.get().onDemand(UserDAO.class);
    }


}
