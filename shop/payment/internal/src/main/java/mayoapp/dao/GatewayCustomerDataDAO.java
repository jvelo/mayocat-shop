/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.UUID;

import org.mayocat.shop.payment.model.GatewayCustomerData;
import org.mayocat.shop.payment.store.jdbi.mapper.PaymentOperationMapper;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonArgumentFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * Doc goes here.
 *
 * @version $Id$
 */
@RegisterMapper(PaymentOperationMapper.class)
@RegisterArgumentFactory({ MapAsJsonArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class GatewayCustomerDataDAO implements Transactional<GatewayCustomerDataDAO>
{
    @SqlQuery
    public abstract GatewayCustomerData getCustomerData(@Bind("customerId") UUID customerId,
            @Bind("gateway") String gateway);

    @SqlUpdate
    public abstract void createCustomerData(@BindBean("gatewayCustomerData") GatewayCustomerData gatewayCustomerData);

    @SqlUpdate
    public abstract void updateCustomerData(@BindBean("gatewayCustomerData") GatewayCustomerData gatewayCustomerData);
}
