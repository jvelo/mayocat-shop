/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.UUID;

import org.mayocat.shop.invoicing.model.InvoiceNumber;
import org.mayocat.shop.invoicing.store.jdbi.mapper.InvoiceNumberMapper;
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
@UseStringTemplate3StatementLocator
@RegisterMapper(InvoiceNumberMapper.class)
public abstract class InvoiceNumberDao implements Transactional<InvoiceNumberDao>
{
    @SqlUpdate
    public abstract void createInvoiceNumber(@BindBean("invoiceNumber") InvoiceNumber number);

    @SqlQuery
    public abstract InvoiceNumber findForOrderId(@Bind("orderId") UUID orderId);

    @SqlQuery
    public abstract Integer getCountForPrefix(@Bind("prefix") String prefix);
}
