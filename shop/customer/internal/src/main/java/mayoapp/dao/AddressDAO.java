/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.UUID;

import org.mayocat.shop.customer.store.jdbi.mapper.AddressMapper;
import org.mayocat.shop.customer.model.Address;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(AddressMapper.class)
@UseStringTemplate3StatementLocator
public abstract class AddressDAO implements Transactional<AddressDAO>
{
    @SqlUpdate
    public abstract void createAddress(@BindBean("address") Address address);

    @SqlQuery
    public abstract Address findById(@Bind("id") UUID id);

    @SqlQuery
    public abstract Address findByCustomerIdAndType(@Bind("customerId") UUID customerId, @Bind("type") String type);

    @SqlUpdate
    public abstract void updateAddress(@BindBean("address") Address address);

    @SqlUpdate
    public abstract void deleteAddress(@BindBean("address") Address address);
}
