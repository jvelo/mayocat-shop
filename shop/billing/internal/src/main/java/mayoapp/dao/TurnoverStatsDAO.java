/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.billing.model.stats.TurnoverStatEntry;
import org.mayocat.shop.billing.store.jdbi.mapper.TurnoverStatEntryMapper;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(TurnoverStatEntryMapper.class)
@UseStringTemplate3StatementLocator
public interface TurnoverStatsDAO
{
    @SqlQuery
    TurnoverStatEntry daily(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    TurnoverStatEntry weekly(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    TurnoverStatEntry monthly(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    TurnoverStatEntry forever(@BindBean("tenant") Tenant tenant);
}
