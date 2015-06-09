/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.jdbi.mapper;

import com.google.common.base.Predicates;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.mayocat.model.Association;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
public class OrderMapper extends AbstractOrderMapper implements ResultSetMapper<Order>
{
    @Override
    public Order map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Order order = new Order();
        fillOrderSummary(resultSet, order);

        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            List<Map<String, Object>> itemsData = mapper.readValue(resultSet.getString("items"),
                    new TypeReference<List<Map<String, Object>>>() {});

            List<OrderItem> items = FluentIterable.from(itemsData).transform(
                    new Function<Map<String, Object>, OrderItem>()
                    {
                        public OrderItem apply(Map<String, Object> map) {
                            if (map == null) {
                                return null;
                            }
                            OrderItem orderItem = new OrderItem();
                            orderItem.setId(UUID.fromString((String) map.get("id")));
                            orderItem.setOrderId(UUID.fromString((String) map.get("order_id")));
                            if (map.containsKey("purchasable_id") && map.get("purchasable_id") != null) {
                                // There might not be a purchasable id
                                orderItem.setPurchasableId(UUID.fromString((String) map.get("purchasable_id")));
                            }
                            orderItem.setType((String) map.get("type"));
                            orderItem.setTitle((String) map.get("title"));
                            orderItem.setMerchant((String) map.get("merchant"));
                            orderItem.setQuantity(((Integer) map.get("quantity")).longValue());
                            orderItem.setUnitPrice(BigDecimal.valueOf((Double) map.get("unit_price")));
                            orderItem.setItemTotal(BigDecimal.valueOf((Double) map.get("item_total")));
                            if (map.containsKey("vat_rate") && map.get("vat_rate") != null) {
                                // There might not be a VAT rate
                                orderItem.setVatRate(BigDecimal.valueOf((Double) map.get("vat_rate")));
                            }
                            if (map.containsKey("data") && map.get("data") != null) {
                                // There might not be data
                                orderItem.addData((Map<String, Object>) map.get("data"));
                            }
                            return orderItem;
                        }
                    }).filter(Predicates.notNull()).toList();
            order.setOrderItems(items);
        } catch (IOException e) {
            logger.error("Failed to deserialize order data", e);
        }

        try {
            resultSet.findColumn("email");
            Customer customer = new Customer();
            customer.setId(order.getCustomerId());
            customer.setSlug(resultSet.getString("customer_slug"));
            customer.setEmail(resultSet.getString("email"));
            customer.setFirstName(resultSet.getString("first_name"));
            customer.setLastName(resultSet.getString("last_name"));
            customer.setPhoneNumber(resultSet.getString("phone_number"));
            order.setCustomer(customer);
        } catch (SQLException e) {
            // Nevermind
        }

        try {
            if (resultSet.getObject("billing_address_id") != null) {
                resultSet.findColumn("billing_address_full_name");
                Address billing = new Address();
                billing.setId((UUID) resultSet.getObject("billing_address_id"));
                billing.setFullName(resultSet.getString("billing_address_full_name"));
                billing.setStreet(resultSet.getString("billing_address_street"));
                billing.setStreetComplement(resultSet.getString("billing_address_street_complement"));
                billing.setZip(resultSet.getString("billing_address_zip"));
                billing.setCity(resultSet.getString("billing_address_city"));
                billing.setCountry(resultSet.getString("billing_address_country"));
                billing.setNote(resultSet.getString("billing_address_note"));
                order.setBillingAddress(billing);
            }
        } catch (SQLException e) {
            // Nevermind
        }

        try {
            if (resultSet.getObject("delivery_address_id") != null) {
                resultSet.findColumn("delivery_address_full_name");
                Address delivery = new Address();
                delivery.setId((UUID) resultSet.getObject("delivery_address_id"));
                delivery.setFullName(resultSet.getString("delivery_address_full_name"));
                delivery.setStreet(resultSet.getString("delivery_address_street"));
                delivery.setStreetComplement(resultSet.getString("delivery_address_street_complement"));
                delivery.setZip(resultSet.getString("delivery_address_zip"));
                delivery.setCity(resultSet.getString("delivery_address_city"));
                delivery.setCountry(resultSet.getString("delivery_address_country"));
                delivery.setNote(resultSet.getString("delivery_address_note"));
                order.setDeliveryAddress(delivery);
            }
        } catch (SQLException e) {
            // Nevermind
        }

        return order;
    }
}
