package org.mayocat.shop.store.rdbms.dbi;

import javax.inject.Inject;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.mayocat.shop.store.rdbms.dbi.dao.TenantDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints={"jdbi", "default"})
public class DBITenantStore implements TenantStore, Initializable
{

    @Inject
    private DBIProvider dbi;

    private TenantDAO dao;
    
    @Override
    public void create(Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException, StoreException
    {
        this.dao.create(tenant);
    }

    @Override
    public void update(Tenant entity) throws InvalidEntityException, StoreException
    {
        
    }

    @Override
    public Tenant findById(Long id) throws StoreException
    {
        return null;
    }

    @Override
    public Tenant findBySlug(String slug) throws StoreException
    {
        return this.dao.findBySlug(slug);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(TenantDAO.class);
    }

}
