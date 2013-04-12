package org.mayocat.shop.billing.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.model.Order;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class
        OrderMapper implements ResultSetMapper<Order>
{
    @Override
    public Order map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Order order = new Order();

        order.setId(resultSet.getLong("id"));

        order.setBillingAddressId(resultSet.getLong("billing_address_id"));
        order.setDeliveryAddressId(resultSet.getLong("delivery_address_id"));
        order.setCustomerId(resultSet.getLong("customer_id"));

        order.setCreationDate(resultSet.getDate("creation_date"));
        order.setUpdateDate(resultSet.getDate("update_date"));

        order.setNumberOfItems(resultSet.getLong("number_of_items"));
        order.setCurrency(Currency.getInstance(resultSet.getString("currency")));

        order.setGrandTotal(resultSet.getBigDecimal("grand_total"));
        order.setItemsTotal(resultSet.getBigDecimal("items_total"));

        order.setStatus(Order.Status.valueOf(resultSet.getString("status")));

        return order;
    }
}
