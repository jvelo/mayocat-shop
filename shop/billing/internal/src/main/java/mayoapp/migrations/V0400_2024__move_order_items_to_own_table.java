/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.migrations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

/**
 * @version $Id$
 */
public class V0400_2024__move_order_items_to_own_table implements JdbcMigration
{
    @Override
    public void migrate(Connection connection) throws Exception
    {
        connection.setAutoCommit(false);

        Statement queryStatement = connection.createStatement();
        ResultSet data = queryStatement.executeQuery("SELECT * from purchase_order");

        List<Order> orders = Lists.newArrayList();
        List<OrderItem> orderItems = Lists.newArrayList();

        ObjectMapper mapper = new ObjectMapper();

        while (data.next()) {
            Order order = new Order();
            order.setId((UUID) data.getObject("entity_id"));

            String orderDataString = data.getString("order_data");
            Map<String, Object> orderData = mapper.readValue(orderDataString, new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");

            for (Map<String, Object> item : items) {
                OrderItem orderItem = new OrderItem();

                orderItem.setId(UUID.randomUUID());
                orderItem.setOrderId(order.getId());

                if (item.containsKey("id") && String.class.isAssignableFrom(item.get("id").getClass())) {
                    orderItem.setPurchasableId(UUID.fromString((String) item.get("id")));
                }
                orderItem.setType((String) item.get("type"));
                orderItem.setTitle((String) item.get("title"));
                orderItem.setQuantity(((Integer) item.get("quantity")).longValue());
                orderItem.setUnitPrice(BigDecimal.valueOf((Double) item.get("unitPrice")));
                orderItem.setItemTotal(BigDecimal.valueOf((Double) item.get("itemTotal")));
                if (item.containsKey("vatRate")) {
                    orderItem.setVatRate(BigDecimal.valueOf((Double) item.get("vatRate")));
                }

                if (item.containsKey("addons")) {
                    orderItem.addData("addons", convertAddonsToMap((List<Map<String, Object>>) item.get("addons")));
                }

                orderItems.add(orderItem);
            }

            orderData.remove("items");
            order.setOrderData(orderData);
            orders.add(order);
        }

        queryStatement.close();

        // 1. Update orders

        PreparedStatement updateOrders = connection.prepareStatement(
                "UPDATE purchase_order SET order_data = CAST (? AS json) WHERE entity_id =?");

        for (Order order : orders) {
            updateOrders.setObject(1, mapper.writeValueAsString(order.getOrderData()));
            updateOrders.setObject(2, order.getId());
            updateOrders.addBatch();
        }

        updateOrders.executeBatch();

        // 2. Insert items

        PreparedStatement insertItems = connection.prepareStatement(
                "INSERT INTO purchase_order_item (id, order_id, purchasable_id, type, title, quantity, unit_price, " +
                        "item_total, vat_rate, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CAST (? as json))");

        for (OrderItem item : orderItems) {
            insertItems.setObject(1, item.getId());
            insertItems.setObject(2, item.getOrderId());
            insertItems.setObject(3, item.getPurchasableId());
            insertItems.setString(4, item.getType());
            insertItems.setString(5, item.getTitle());
            insertItems.setLong(6, item.getQuantity());
            insertItems.setBigDecimal(7, item.getUnitPrice());
            insertItems.setBigDecimal(8, item.getItemTotal());
            insertItems.setBigDecimal(9, item.getVatRate());
            insertItems.setString(10, mapper.writeValueAsString(item.getData()));
            insertItems.addBatch();
        }

        insertItems.executeBatch();
    }

    private Map<String, Object> convertAddonsToMap(List<Map<String, Object>> addons)
    {
        Map<String, Object> map = Maps.newHashMap();

        for (Map<String, Object> addon : addons) {
            if (!map.containsKey(addon.get("group"))) {
                map.put((String) addon.get("group"), Maps.newHashMap());
            }
            ((Map<String, Object>) map.get(addon.get("group"))).put((String) addon.get("name"), addon.get("value"));
        }

        return map;
    }
}
