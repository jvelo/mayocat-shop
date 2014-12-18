/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.billing.store.jdbi.argument.CurrencyAsCurrencyCodeArgumentFactory;
import org.mayocat.shop.billing.store.jdbi.argument.OrderStatusArgumentFactory;
import org.mayocat.shop.billing.store.jdbi.mapper.OrderMapper;
import org.mayocat.store.rdbms.dbi.argument.DateAsTimestampArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonStringArgumentFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
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

    @SqlBatch
    public abstract void insertOrderItems(@BindBean("orderItem") List<OrderItem> orderItem);

    @SqlUpdate
    public abstract Integer updateOrder(@BindBean("order") Order order);

    @SqlQuery
    public abstract List<Order> findAllPaidOrAwaitingPayment(@Bind("number") Integer number,
            @Bind("offset") Integer offset, @Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract Integer countAllPaidOrAwaitingPayment(@Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract Order findBySlugWithCustomer(@Bind("slug") String slug, @Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract Order findByIdWithCustomer(@Bind("id") UUID id);

    @SqlQuery
    public abstract Integer lastOrderNumber(@Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract List<Order> findAllPaidForCustomer(@Bind("customerId") UUID customerId);

    @SqlQuery
    public abstract List<Order> findAllPaidForCustomerPaginated(@Bind("customerId") UUID customerId,
            @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllPaidForCustomer(@Bind("customerId") UUID customerId);

    @SqlQuery
    public abstract List<Order> findAllPaidBetween(@Bind("date1") Date date1, @Bind("date2") Date date2);
}
