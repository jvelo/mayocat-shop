package org.mayocat.shop.store.rdbms.dbi;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.TenantConfiguration;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.mayocat.shop.store.rdbms.dbi.dao.TenantDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

@Component(hints={"jdbi", "default"})
public class DBITenantStore implements TenantStore, Initializable
{

    @Inject
    private DBIProvider dbi;

    private TenantDAO dao;
    
    @Override
    public void create(Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException
    {
        this.dao.begin();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());

        TenantConfiguration configuration = tenant.getConfiguration();

        try {
            String configurationAsJson = mapper.writeValueAsString(configuration);
            Integer configurationId = this.dao.createConfiguration(configuration.getVersion(), configurationAsJson);
            this.dao.create(tenant, configurationId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("", e);
        } finally {
            this.dao.commit();
        }
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
