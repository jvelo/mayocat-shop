/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.billing.model.OrderSummary;
import org.skife.jdbi.v2.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

/**
 * @version $Id$
 */
public class AbstractOrderMapper
{
    final Logger logger = LoggerFactory.getLogger(OrderSummaryMapper.class);

    protected void fillOrderSummary(ResultSet resultSet, OrderSummary order) throws SQLException
    {
        order.setId((UUID) resultSet.getObject("id"));
        order.setSlug(resultSet.getString("slug"));

        order.setBillingAddressId((UUID) resultSet.getObject("billing_address_id"));
        order.setDeliveryAddressId((UUID) resultSet.getObject("delivery_address_id"));
        order.setCustomerId((UUID) resultSet.getObject("customer_id"));

        order.setCreationDate(resultSet.getTimestamp("creation_date"));
        order.setUpdateDate(resultSet.getTimestamp("update_date"));

        order.setNumberOfItems(resultSet.getLong("number_of_items"));
        order.setCurrency(Currency.getInstance(resultSet.getString("currency")));

        order.setItemsTotal(resultSet.getBigDecimal("items_total"));
        order.setItemsTotalExcl(resultSet.getBigDecimal("items_total_excl"));
        order.setShipping(resultSet.getBigDecimal("shipping"));
        order.setShippingExcl(resultSet.getBigDecimal("shipping_excl"));
        order.setGrandTotal(resultSet.getBigDecimal("grand_total"));
        order.setGrandTotalExcl(resultSet.getBigDecimal("grand_total_excl"));

        order.setStatus(OrderSummary.Status.valueOf(resultSet.getString("status")));
        order.setAdditionalInformation(resultSet.getString("additional_information"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        try {
            Map<String, Object> data = mapper.readValue(resultSet.getString("order_data"),
                    new TypeReference<Map<String, Object>>()
                    {
                    });
            order.setOrderData(data);
        } catch (IOException e) {
            logger.error("Failed to deserialize order data", e);
        }
    }
}
