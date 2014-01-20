/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.payment.model.GatewayCustomerData;
import org.mayocat.shop.payment.model.GatewayTenantData;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

/**
 * Mapper for {@link GatewayTenantData}
 *
 * @version $Id$
 */
public class GatewayTenantDataMapper implements ResultSetMapper<GatewayTenantData>
{
    @Override
    public GatewayTenantData map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        UUID tenantId = (UUID) resultSet.getObject("tenant_id");
        String gateway = resultSet.getString("gateway");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        try {
            Map<String, Object> data = mapper.readValue(resultSet.getString("tenant_data"),
                    new TypeReference<Map<String, Object>>() {});

            return new GatewayTenantData(tenantId, gateway, data);
        } catch (IOException e) {
            final Logger logger = LoggerFactory.getLogger(GatewayTenantDataMapper.class);
            logger.error("Failed to de-serialize gateway tenant data", e);

            return null;
        }
    }
}
