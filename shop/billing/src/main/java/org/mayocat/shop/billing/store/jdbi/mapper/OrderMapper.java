package org.mayocat.shop.billing.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Map;

import org.mayocat.model.PerhapsLoaded;
import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.model.Order;
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
public class OrderMapper implements ResultSetMapper<Order>
{
    @Override
    public Order map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Order order = new Order();

        order.setId(resultSet.getLong("id"));
        order.setSlug(resultSet.getString("slug"));

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

        try {
            resultSet.findColumn("email");
            Customer customer = new Customer();
            customer.setId(order.getCustomerId());
            customer.setEmail(resultSet.getString("email"));
            customer.setFirstName(resultSet.getString("first_name"));
            customer.setLastName(resultSet.getString("last_name"));
            order.setCustomer(new PerhapsLoaded(customer));
        } catch (SQLException e) {
            // Nevermind
        }

        try {
            resultSet.findColumn("billing_address_full_name");
            Address billing = new Address();
            billing.setFullName(resultSet.getString("billing_address_full_name"));
            billing.setStreet(resultSet.getString("billing_address_street"));
            billing.setStreetComplement(resultSet.getString("billing_address_street_complement"));
            billing.setZip(resultSet.getString("billing_address_zip"));
            billing.setCity(resultSet.getString("billing_address_city"));
            billing.setCountry(resultSet.getString("billing_address_country"));
            order.setBillingAddress(new PerhapsLoaded<Address>(billing));
        } catch (SQLException e) {
            // Nevermind
        }

        try {
            resultSet.findColumn("delivery_address_full_name");
            Address delivery = new Address();
            delivery.setFullName(resultSet.getString("delivery_address_full_name"));
            delivery.setStreet(resultSet.getString("delivery_address_street"));
            delivery.setStreetComplement(resultSet.getString("delivery_address_street_complement"));
            delivery.setZip(resultSet.getString("delivery_address_zip"));
            delivery.setCity(resultSet.getString("delivery_address_city"));
            delivery.setCountry(resultSet.getString("delivery_address_country"));
            order.setDeliveryAddress(new PerhapsLoaded<Address>(delivery));
        } catch (SQLException e) {
            // Nevermind
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        try {
            Map<String, Object> data = mapper.readValue(resultSet.getString("order_data"),
                    new TypeReference<Map<String, Object>>(){});
            order.setOrderData(data);
        } catch (IOException e) {
            final Logger logger = LoggerFactory.getLogger(OrderMapper.class);
            logger.error("Failed to deserialize order data", e);
        }

        return order;
    }
}
