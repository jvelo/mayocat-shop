/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.accounts.store.TenantStore;
import mayoapp.dao.TenantDAO;

import org.mayocat.context.WebContext;
import org.mayocat.model.AddonGroup;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mayocat.addons.util.AddonUtils.asMap;

@Component(hints = { "jdbi", "default" })
public class DBITenantStore implements TenantStore, Initializable
{
    private static final String TENANT_TABLE_NAME = "tenant";

    @Inject
    private WebContext context;

    @Inject
    private DBIProvider dbi;

    private TenantDAO dao;

    @Override
    public Tenant create(Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(TENANT_TABLE_NAME, tenant.getSlug()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        TenantConfiguration configuration = tenant.getConfiguration();

        try {
            String configurationAsJson = convertConfigurationToJSON(configuration);

            UUID id = UUID.randomUUID();
            tenant.setId(id);
            this.dao.createEntity(tenant, TENANT_TABLE_NAME);
            this.dao.create(tenant, configuration.getVersion(), configurationAsJson);
            this.dao.createOrUpdateAddons(tenant);

            return tenant;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create tenant", e);
        } finally {
            this.dao.commit();
        }
    }

    @Override
    public void update(Tenant tenant) throws InvalidEntityException, EntityDoesNotExistException
    {
        this.dao.begin();

        Tenant originalProduct = this.findBySlug(tenant.getSlug());
        if (originalProduct == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }
        tenant.setId(originalProduct.getId());
        Integer updatedRows = this.dao.update(tenant);
        this.dao.createOrUpdateAddons(tenant);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating tenant");
        }
    }

    @Override
    public void delete(@Valid Tenant entity) throws EntityDoesNotExistException
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAllTenants();
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
        return this.dao.findAll(number, offset);
    }

    @Override
    public List<Tenant> findByIds(List<UUID> ids)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Tenant findById(UUID id)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Tenant findBySlug(String slug)
    {
        Tenant tenant = this.dao.findBySlug("tenant", slug);
        if (tenant != null) {
            List<AddonGroup> addons = this.dao.findAddons(tenant);
            tenant.setAddons(asMap(addons));
        }
        return tenant;
    }

    @Override
    public Tenant findByDefaultHost(String host)
    {
        Tenant tenant = this.dao.findByDefaultHost(host);
        if (tenant != null) {
            List<AddonGroup> addons = this.dao.findAddons(tenant);
            tenant.setAddons(asMap(addons));
        }
        return tenant;
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(TenantDAO.class);
    }

    private Tenant getTenant()
    {
        return this.context.getTenant();
    }

    private String convertConfigurationToJSON(TenantConfiguration configuration) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(configuration);
    }
}
