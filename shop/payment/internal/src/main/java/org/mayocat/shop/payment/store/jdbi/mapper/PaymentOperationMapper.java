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

import org.mayocat.shop.payment.model.PaymentOperation;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

/**
 * @version $Id$
 */
public class PaymentOperationMapper implements ResultSetMapper<PaymentOperation>
{
    @Override
    public PaymentOperation map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        PaymentOperation operation = new PaymentOperation();
        operation.setId((UUID) resultSet.getObject("id"));
        operation.setOrderId((UUID) resultSet.getObject("order_id"));
        operation.setExternalId(resultSet.getString("id"));
        operation.setGatewayId(resultSet.getString("gateway_id"));
        operation.setResult(PaymentOperation.Result.valueOf(resultSet.getString("result")));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        try {
            Map<String, Object> data = mapper.readValue(resultSet.getString("memo"),
                    new TypeReference<Map<String, Object>>(){});
            operation.setMemo(data);
        } catch (IOException e) {
            final Logger logger = LoggerFactory.getLogger(PaymentOperationMapper.class);
            logger.error("Failed to de-serialize payment operation memo", e);
        }

        return operation;
    }
}
