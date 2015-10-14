/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.store.jdbi;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.accounts.store.jdbi.mapper.TenantMapper;
import org.mayocat.model.Entity;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.marketplace.model.EntityAndTenant;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Strings;

/**
 * @version $Id$
 */
public abstract class AbstractEntityAndTenantMapper implements ResultSetMapper<EntityAndTenant<Product>>
{
    @Override
    public EntityAndTenant<Product> map(int index, ResultSet result, StatementContext ctx) throws SQLException
    {
        Product product = extractEntity(index, result, ctx);

        String slug = result.getString("tenant_entity_slug");
        String defaultHost = result.getString("tenant_entity_default_host");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        Integer configurationVersion = result.getInt("tenant_entity_configuration_version");
        TenantConfiguration configuration;
        if (Strings.isNullOrEmpty(result.getString("tenant_entity_configuration"))) {
            configuration = new TenantConfiguration(configurationVersion, Collections.<String, Serializable>emptyMap());
        } else {
            try {
                Map<String, Serializable> data = mapper.readValue(result.getString("tenant_entity_configuration"),
                        new TypeReference<Map<String, Object>>(){});
                configuration = new TenantConfiguration(configurationVersion, data);
            } catch (IOException e) {
                final Logger logger = LoggerFactory.getLogger(TenantMapper.class);
                logger.error("Failed to load configuration for tenant with slug [{}]", e);
                configuration = new TenantConfiguration();
            }
        }

        Tenant tenant = new Tenant((UUID) result.getObject("tenant_entity_id"), slug, configuration);
        tenant.setFeaturedImageId((UUID) result.getObject("tenant_entity_featured_image_id"));
        tenant.setSlug(slug);
        tenant.setDefaultHost(defaultHost);
        tenant.setCreationDate(result.getTimestamp("tenant_entity_creation_date"));
        tenant.setName(result.getString("tenant_entity_name"));
        tenant.setDescription(result.getString("tenant_entity_description"));
        tenant.setContactEmail(result.getString("tenant_entity_contact_email"));

        return new EntityAndTenant<>(product, tenant);
    }

    public abstract <E extends Entity> E extractEntity(int index, ResultSet result, StatementContext ctx)
            throws SQLException;
}
