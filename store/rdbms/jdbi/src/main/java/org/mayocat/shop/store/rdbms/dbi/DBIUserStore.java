package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.mayocat.shop.store.rdbms.dbi.dao.UserDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = {"jdbi", "default"})
public class DBIUserStore implements UserStore, Initializable
{
    @Inject
    private DBIProvider dbi;

    private UserDAO dao;

    public void create(User user, Tenant tenant, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException,
        StoreException
    {
        if (this.dao.findBySlug(user.getSlug(), tenant) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();
                
        this.dao.createEntity(user, "user", tenant);
        Long entityId = this.dao.getId(user, "user", tenant);
        this.dao.create(entityId, user);
        this.dao.addRoleToUser(entityId, initialRole.toString());
        
        this.dao.commit();
    }

    public void update(User user, Tenant tenant) throws EntityDoesNotExistsException, InvalidEntityException,
        StoreException
    {
        if (this.dao.findBySlug(user.getSlug(), tenant) != null) {
            throw new EntityDoesNotExistsException();
        }
        this.dao.update(user, tenant);
    }

    public User findById(Long id) throws StoreException
    {
        return this.dao.findById(id);
    }

    public List<User> findAll(Tenant tenant, Integer number, Integer offset) throws StoreException
    {
        return this.dao.findAll(tenant, number, offset);
    }

    public User findByEmailOrUserNameAndTenant(String userNameOrEmail, Tenant t) throws StoreException
    {
        return this.dao.findByEmailOrUserNameAndTenant(userNameOrEmail, t);
    }

    public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(UserDAO.class);
    }

    public void create(User user) throws EntityAlreadyExistsException, InvalidEntityException, StoreException
    {
        // FIXME KILL ME
        Tenant t = new Tenant(new Long(1));
        t.setSlug("shop");
        this.create(user, t, Role.ADMIN);
    }

    public void update(User entity) throws InvalidEntityException, StoreException
    {
        // TODO Auto-generated method stub

    }

    public List<Role> findRolesForUser(User user) throws StoreException
    {
        return this.dao.findRolesForUser(user);
    }

}
