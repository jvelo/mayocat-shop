/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.shipping.Strategy;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.model.CarrierRule;
import org.mayocat.shop.shipping.store.jdbi.BindCarrier;
import org.mayocat.shop.shipping.store.jdbi.argument.StrategyArgumentFactory;
import org.mayocat.shop.shipping.store.jdbi.mapper.CarrierMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 * @version $Id$
 */
@UseStringTemplate3StatementLocator
@RegisterArgumentFactory(StrategyArgumentFactory.class)
@RegisterMapper(CarrierMapper.class)
public interface CarrierDAO extends Transactional<CarrierDAO>
{
    @SqlQuery
    // returned as a Set so that carrier rules get folded.
    // FIXME. Find a better way to fold joined rows.
    // Follow https://github.com/brianm/jdbi/pull/69
    public Set<Carrier> findById(@Bind("id") UUID id);

    @SqlQuery
    public Set<Carrier> findAll(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public Set<Carrier> findAllWithStrategy(@BindBean("tenant") Tenant tenant, @Bind("strategy") Strategy strategy);

    @SqlUpdate
    public abstract void create(@BindCarrier("carrier") Carrier carrier);

    @SqlUpdate
    public abstract void delete(@BindCarrier("carrier") Carrier carrier);

    @SqlUpdate
    public abstract void update(@BindCarrier("carrier") Carrier carrier);

    @SqlUpdate
    public abstract int updateRule(@Bind("carrierId") UUID carrierId, @BindBean("rule") CarrierRule rule);

    @SqlUpdate
    public abstract void addRule(@Bind("carrierId") UUID carrierId, @BindBean("rule") CarrierRule rule);

    @SqlUpdate
    public abstract void removeRules(@Bind("carrierId") UUID carrierId,
            @BindIn("excludedUpToValues") Collection<BigDecimal> excludedUpToValues);
}
