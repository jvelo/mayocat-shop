/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.mayocat.shop.shipping.Strategy;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.model.CarrierRule;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class CarrierMapper implements ResultSetMapper<Carrier>
{
    @Override
    public Carrier map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        Carrier carrier = null;
        UUID thisRowId = (UUID) r.getObject("id");
        if (ctx.getAttribute("__accumulator") != null) {
            carrier = (Carrier) ctx.getAttribute("__accumulator");
            if (!carrier.getId().equals(thisRowId)) {
                carrier = new Carrier();
            }
        }
        if (carrier == null) {
            carrier = new Carrier();
        }
        carrier.setId(thisRowId);
        carrier.setTitle(r.getString("title"));
        carrier.setDescription(r.getString("description"));
        carrier.setId((UUID) r.getObject("id"));
        carrier.setTenantId((UUID) r.getObject("tenant_id"));
        carrier.setMinimumDays(r.getInt("minimum_days"));
        carrier.setMaximumDays(r.getInt("maximum_days"));
        carrier.setStrategy(Strategy.fromJson(r.getString("strategy")));
        carrier.setPerShipping(r.getBigDecimal("per_shipping"));
        carrier.setPerItem(r.getBigDecimal("per_item"));
        carrier.setPerAdditionalUnit(r.getBigDecimal("per_additional_unit"));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            carrier.setDestinations(objectMapper
                    .<List<String>>readValue(r.getString("destinations"), new TypeReference<List<String>>(){}));
        } catch (IOException e) {
            throw new SQLException("Failed to de-serialize carrier destinations", e);
        }

        if (r.getBigDecimal("price") != null) {
            CarrierRule rule = new CarrierRule();
            rule.setUpToValue(r.getBigDecimal("up_to_value"));
            rule.setPrice(r.getBigDecimal("price"));
            carrier.addRule(rule);
        }

        ctx.setAttribute("__accumulator", carrier);
        return carrier;
    }
}
