package org.mayocat.shop.store.rdbms.dbi;

import java.util.Collections;
import java.util.List;

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
    public void create(Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException
    {
        this.dao.create(tenant);
    }

    @Override
    public void update(Tenant entity) throws InvalidEntityException
    {
        
    }

    @Override
    public List<Tenant> findAll(Integer number, Integer offset)
    {
        // TODO
        return Collections.emptyList();
    }

    @Override
    public Tenant findById(Long id)
    {
        return null;
    }

    @Override
    public Tenant findBySlug(String slug)
    {
        return this.dao.findBySlug(slug);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(TenantDAO.class);
    }

}
