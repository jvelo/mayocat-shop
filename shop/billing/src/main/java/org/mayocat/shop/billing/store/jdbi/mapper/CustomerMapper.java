package org.mayocat.shop.billing.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.shop.billing.model.Customer;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */

public class CustomerMapper implements ResultSetMapper<Customer>
{
    @Override
    public Customer map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Customer customer = new Customer();

        customer.setEmail(resultSet.getString("email"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setId((UUID) resultSet.getObject("id"));

        return customer;
    }
}
