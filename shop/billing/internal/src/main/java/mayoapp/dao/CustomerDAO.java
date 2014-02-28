/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.store.jdbi.mapper.CustomerMapper;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(CustomerMapper.class)
@UseStringTemplate3StatementLocator
public abstract class CustomerDAO implements EntityDAO<Customer>, Transactional<CustomerDAO>
{
    @SqlUpdate
    public abstract void create(@BindBean("customer") Customer customer);

    @SqlUpdate
    public abstract void updateCustomer(@BindBean("customer") Customer customer);
}
