/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.jdbi.mapper.OrderMapper;
import org.mayocat.shop.billing.store.jdbi.argument.CurrencyAsCurrencyCodeArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.DateAsTimestampArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonStringArgumentFactory;
import org.mayocat.shop.billing.store.jdbi.argument.OrderStatusArgumentFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(OrderMapper.class)
@RegisterArgumentFactory({ DateAsTimestampArgumentFactory.class, CurrencyAsCurrencyCodeArgumentFactory.class,
        OrderStatusArgumentFactory.class, MapAsJsonStringArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class OrderDAO implements EntityDAO<Order>, Transactional<OrderDAO>
{
    @SqlUpdate
    public abstract void createOrder(@BindBean("order") Order order);

    @SqlUpdate
    public abstract Integer updateOrder(@BindBean("order") Order order);

    @SqlQuery
    public abstract List<Order> findAllPaidOrAwaitingPayment(@Bind("number") Integer number,
            @Bind("offset") Integer offset,
            @BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract Order findBySlugWithCustomer(@Bind("slug") String slug, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract Order findByIdWithCustomer(@Bind("id") UUID id);

    @SqlQuery
    public abstract Integer lastOrderNumber(@BindBean("tenant") Tenant tenant);
}
