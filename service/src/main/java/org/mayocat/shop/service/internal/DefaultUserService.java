package org.mayocat.shop.service.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.authorization.PasswordManager;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.service.UserService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultUserService implements UserService
{
    @Inject
    private Provider<UserStore> userStore;

    @Inject
    private PasswordManager passwordManager;

    public List<User> findAll(int number, int offset) throws StoreException
    {
        return this.userStore.get().findAll(number, offset);
    }

    public void create(User user) throws InvalidEntityException, EntityAlreadyExistsException, StoreException
    {
        this.create(user, null);
    }
    
    public void create(User user, Role initialRole) throws InvalidEntityException, EntityAlreadyExistsException,
        StoreException
    {
        user.setPassword(this.passwordManager.hashPassword(user.getPassword()));

        this.userStore.get().create(user, initialRole);
    }

    public void update(User entity) throws InvalidEntityException, StoreException
    {
        this.userStore.get().update(entity);
    }

    public void createInitialUser(User user) throws StoreException
    {
        if (this.hasUsers()) {
            throw new RuntimeException("Illegal attempt at create the initial user");
        }

        try {
            this.create(user, Role.ADMIN);
        } catch (EntityAlreadyExistsException e1) {
            throw new StoreException(e1);
        } catch (InvalidEntityException e2) {
            throw new StoreException(e2);
        }
    }

    public boolean hasUsers() throws StoreException
    {
        return this.findAll(1, 0).size() > 0;
    }

    public User findBySlug(String slug) throws StoreException
    {
        return this.findByEmailOrUserName(slug);
    }

    public User findByEmailOrUserName(String userNameOrEmail) throws StoreException
    {
        return this.userStore.get().findByEmailOrUserName(userNameOrEmail);
    }



}
