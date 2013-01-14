package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.TenantConfiguration;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.TenantStore;
import org.mayocat.shop.store.rdbms.dbi.dao.TenantDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(hints = { "jdbi", "default" })
public class DBITenantStore implements TenantStore, Initializable
{
    @Inject
    private Execution execution;

    @Inject
    private DBIProvider dbi;

    private TenantDAO dao;

    @Override
    public void create(Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException
    {
        this.dao.begin();

        TenantConfiguration configuration = tenant.getConfiguration();

        try {
            String configurationAsJson = convertConfigurationToJSON(configuration);
            Integer configurationId = this.dao.createConfiguration(configuration.getVersion(), configurationAsJson);
            this.dao.create(tenant, configurationId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create tenant", e);
        } finally {
            this.dao.commit();
        }
    }

    @Override
    public void update(Tenant entity) throws InvalidEntityException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void updateConfiguration(TenantConfiguration configuration)
    {
        try {
            this.dao.updateConfiguration(getTenant(), configuration.getVersion(),
                    convertConfigurationToJSON(configuration));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("", e);
        }
    }

    @Override
    public List<Tenant> findAll(Integer number, Integer offset)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Tenant findById(Long id)
    {
        throw new UnsupportedOperationException("Not implemented");
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

    private Tenant getTenant()
    {
        return this.execution.getContext().getTenant();
    }

    private String convertConfigurationToJSON(TenantConfiguration configuration) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(configuration);
    }
}
