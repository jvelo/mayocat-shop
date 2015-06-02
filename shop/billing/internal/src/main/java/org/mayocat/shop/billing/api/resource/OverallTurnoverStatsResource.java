/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.resource;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mayoapp.dao.TurnoverStatsDAO;
import org.mayocat.accounts.model.Role;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.context.WebContext;
import org.mayocat.rest.Resource;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component("/api/billing/stats")
@Path("/api/billing/stats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized(roles = Role.GOD)
public class OverallTurnoverStatsResource implements Resource, Initializable
{
    @Inject
    private DBIProvider dbi;

    @Inject
    private WebContext webContext;

    private TurnoverStatsDAO statsDAO;

    @GET
    @Authorized
    public Response getStats() {
        Map<String, Object> stats = Maps.newHashMap();

        stats.put("daily", statsDAO.dailyOverall());
        stats.put("weekly", statsDAO.weeklyOverall());
        stats.put("monthly", statsDAO.monthlyOverall());
        stats.put("forever", statsDAO.foreverOverall());

        return Response.ok(stats).build();
    }

    @Override
    public void initialize() throws InitializationException {
        this.statsDAO = dbi.get().onDemand(TurnoverStatsDAO.class);
    }
}
