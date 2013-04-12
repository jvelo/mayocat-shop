package org.mayocat.store.rdbms.dbi.dao;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.jdbi.mapper.OrderMapper;
import org.mayocat.store.rdbms.dbi.argument.CurrencyAsCurrencyCodeArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.DateAsTimestampArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.OrderStatusArgumentFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
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
        OrderStatusArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class OrderDAO implements EntityDAO<Order>, Transactional<OrderDAO>
{
    @GetGeneratedKeys
    @SqlUpdate
    public abstract Long createOrder(@Bind("id") Long entityId, @BindBean("order") Order order);
}
