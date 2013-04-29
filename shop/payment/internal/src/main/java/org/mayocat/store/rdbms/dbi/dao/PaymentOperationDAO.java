package org.mayocat.store.rdbms.dbi.dao;

import java.util.UUID;

import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.jdbi.mapper.PaymentOperationMapper;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.PaymentOperationResultArgumentFactory;
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
@RegisterMapper(PaymentOperationMapper.class)
@RegisterArgumentFactory({ MapAsJsonArgumentFactory.class, PaymentOperationResultArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class PaymentOperationDAO implements Transactional<PaymentOperationDAO>
{
    @GetGeneratedKeys
    @SqlUpdate
    public abstract UUID createPaymentOperation(@BindBean("operation") PaymentOperation operation);
}
