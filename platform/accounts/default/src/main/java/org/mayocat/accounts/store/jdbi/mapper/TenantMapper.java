/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store.jdbi.mapper;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Strings;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class TenantMapper implements ResultSetMapper<Tenant>
{
    @Override
    public Tenant map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        String slug = result.getString("slug");
        String defaultHost = result.getString("default_host");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        Integer configurationVersion = result.getInt("configuration_version");
        TenantConfiguration configuration;
        if (Strings.isNullOrEmpty(result.getString("configuration"))) {
            configuration = new TenantConfiguration(configurationVersion, Collections.<String, Serializable>emptyMap());
        } else {
            try {
                Map<String, Serializable> data = mapper.readValue(result.getString("configuration"),
                        new TypeReference<Map<String, Object>>() {});
                configuration = new TenantConfiguration(configurationVersion, data);
            } catch (IOException e) {
                final Logger logger = LoggerFactory.getLogger(TenantMapper.class);
                logger.error("Failed to load configuration for tenant with slug [{}]", e);
                configuration = new TenantConfiguration();
            }
        }

        Tenant tenant = new Tenant((UUID) result.getObject("id"), slug, configuration);
        tenant.setFeaturedImageId((UUID) result.getObject("featured_image_id"));
        tenant.setSlug(slug);
        tenant.setDefaultHost(defaultHost);
        tenant.setCreationDate(result.getTimestamp("creation_date"));
        tenant.setName(result.getString("name"));
        tenant.setDescription(result.getString("description"));
        tenant.setContactEmail(result.getString("contact_email"));

        return tenant;
    }
}
