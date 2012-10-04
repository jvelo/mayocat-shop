package org.mayocat.shop.service.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.service.RoleService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.RoleStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultRoleService implements RoleService
{

    @Inject
    private Provider<RoleStore> roleStore;

    @Override
    public void create(Role entity) throws InvalidEntityException, EntityAlreadyExistsException, StoreException
    {
        this.roleStore.get().create(entity);
    }

    @Override
    public void update(Role entity) throws InvalidEntityException, StoreException
    {
        this.roleStore.get().update(entity);
    }

    @Override
    public List<Role> findAllByUser(User user) throws StoreException
    {
        return this.roleStore.get().findAllByUser(user);
    }

    @Override
    public List<Role> findAll(int number, int offset) throws StoreException
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }
}
