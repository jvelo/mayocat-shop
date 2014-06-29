/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.jdbi.mapper.PaymentOperationMapper;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonStringArgumentFactory;
import org.mayocat.shop.payment.store.jdbi.argument.PaymentOperationResultArgumentFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(PaymentOperationMapper.class)
@RegisterArgumentFactory({ MapAsJsonStringArgumentFactory.class, PaymentOperationResultArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class PaymentOperationDAO implements Transactional<PaymentOperationDAO>
{
    @SqlUpdate
    public abstract void createPaymentOperation(@BindBean("operation") PaymentOperation operation);
}
