/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.shop.customer.model.Customer;
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

        customer.setId((UUID) resultSet.getObject("id"));
        customer.setSlug(resultSet.getString("slug"));
        customer.setEmail(resultSet.getString("email"));
        customer.setPhoneNumber(resultSet.getString("phone_number"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setCompany(resultSet.getString("company"));
        customer.setUserId((UUID) resultSet.getObject("agent_id"));

        return customer;
    }
}
