/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.store.jdbi;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.WebContext;
import org.mayocat.shop.shipping.Strategy;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.model.CarrierRule;
import org.mayocat.shop.shipping.store.CarrierStore;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import mayoapp.dao.CarrierDAO;

/**
 * @version $Id$
 */
@Component
public class DBICarrierStore implements CarrierStore, Initializable
{
    @Inject
    private WebContext context;

    @Inject
    private DBIProvider dbi;

    private CarrierDAO carrierDAO;

    public Carrier findById(UUID id)
    {
        Iterator<Carrier> carrier = carrierDAO.findById(id).iterator();
        if (carrier.hasNext()) {
            return carrier.next();
        }
        return null;
    }

    public Set<Carrier> findAll()
    {
        return carrierDAO.findAll(getTenant());
    }

    public Set<Carrier> findAll(Strategy strategy)
    {
        return carrierDAO.findAllWithStrategy(getTenant(), strategy);
    }

    public void createCarrier(Carrier carrier)
    {
        carrierDAO.begin();
        carrier.setId(UUID.randomUUID());
        carrier.setTenantId(getTenant().getId());
        this.carrierDAO.create(carrier);
        for (CarrierRule rule : carrier.getRules()) {
            carrierDAO.addRule(carrier.getId(), rule);
        }
        carrierDAO.commit();
    }

    public void updateCarrier(Carrier carrier)
    {
        carrierDAO.begin();
        carrierDAO.update(carrier);
        for (CarrierRule rule : carrier.getRules()) {
            if (carrierDAO.updateRule(carrier.getId(), rule) == 0) {
                carrierDAO.addRule(carrier.getId(), rule);
            }
        }
        Collection<BigDecimal> excluded = Collections2.transform(carrier.getRules(),
                new Function<CarrierRule, BigDecimal>()
                {
                    @Override
                    public BigDecimal apply(final CarrierRule rule)
                    {
                        return rule.getUpToValue();
                    }
                }
        );
        if (excluded.size() > 0) {
            carrierDAO.removeRules(carrier.getId(), excluded);
        }
        carrierDAO.commit();
    }

    public void deleteCarrier(Carrier carrier)
    {
        carrierDAO.delete(carrier);
        // Note: rules deletion is cascaded from carrier
    }

    protected Tenant getTenant()
    {
        return this.context.getTenant();
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.carrierDAO = this.dbi.get().onDemand(CarrierDAO.class);
    }
}
