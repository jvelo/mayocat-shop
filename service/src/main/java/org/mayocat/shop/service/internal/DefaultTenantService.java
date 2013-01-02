package org.mayocat.shop.service.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.service.TenantService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultTenantService implements TenantService
{
    @Inject
    private Provider<TenantStore> tenantStore;

    @Override
    public void create(Tenant entity) throws InvalidEntityException, EntityAlreadyExistsException, StoreException
    {
        this.tenantStore.get().create(entity);
    }

    @Override
    public void update(Tenant entity) throws InvalidEntityException, StoreException
    {
        this.tenantStore.get().update(entity);

    }

    @Override
    public Tenant findBySlug(String slug) throws StoreException
    {
        return this.tenantStore.get().findBySlug(slug);
    }

    @Override
    public Tenant findBySlugOrAlias(String slugOrAlias) throws StoreException
    {
        return this.tenantStore.get().findBySlugOrAlias(slugOrAlias);
    }

    @Override
    public List<Tenant> findAll(int number, int offset) throws StoreException
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

}
