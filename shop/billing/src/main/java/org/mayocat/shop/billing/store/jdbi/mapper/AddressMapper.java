package org.mayocat.shop.billing.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.billing.model.Address;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class AddressMapper implements ResultSetMapper<Address>
{
    @Override
    public Address map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Address address = new Address();
        address.setId(resultSet.getLong("id"));
        address.setCustomerId(resultSet.getLong("customer_id"));

        address.setCompany(resultSet.getString("company"));
        address.setFullName(resultSet.getString("full_name"));
        address.setStreet(resultSet.getString("street"));
        address.setStreetComplement(resultSet.getString("street_complement"));
        address.setZip(resultSet.getString("zip"));
        address.setCity(resultSet.getString("city"));
        address.setCountry(resultSet.getString("country"));

        return address;
    }
}
