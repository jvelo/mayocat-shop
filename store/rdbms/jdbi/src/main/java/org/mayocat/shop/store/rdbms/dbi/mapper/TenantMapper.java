package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import com.google.common.base.Strings;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.TenantConfiguration;
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
        String slug = result.getString("tenant.slug");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        Integer configurationVersion = result.getInt("configuration.version");
        TenantConfiguration configuration;
        if (Strings.isNullOrEmpty(result.getString("configuration.data"))) {
            configuration = new TenantConfiguration(configurationVersion, Collections.<String, Object>emptyMap());
        } else {
            try {
                Map<String, Object> data = mapper.readValue(result.getString("configuration.data"),
                        new TypeReference<Map<String, Object>>() {});
                configuration = new TenantConfiguration(configurationVersion, data);
            } catch (IOException e) {
                final Logger logger = LoggerFactory.getLogger(TenantMapper.class);
                logger.error("Failed to load configuration for tenant with slug [{}]", e);
                configuration = new TenantConfiguration();
            }
        }

        Tenant tenant = new Tenant(result.getLong("tenant.id"), slug, configuration);
        tenant.setSlug(slug);

        return tenant;
    }
}
