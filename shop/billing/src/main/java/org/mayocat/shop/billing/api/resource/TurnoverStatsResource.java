package org.mayocat.shop.billing.api.resource;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.collect.Maps;

import mayoapp.dao.TurnoverStatsDAO;

/**
 * @version $Id$
 */
@Component(TurnoverStatsResource.PATH)
@Path(TurnoverStatsResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class TurnoverStatsResource implements Resource, Initializable
{
    public static final String PATH = API_ROOT_PATH + "billing/stats";

    @Inject
    private DBIProvider dbi;

    private TurnoverStatsDAO statsDAO;

    @GET
    @Authorized
    public Response getStats()
    {
        Map<String, Object> stats = Maps.newHashMap();

        stats.put("daily", statsDAO.daily());
        stats.put("weekly", statsDAO.weekly());
        stats.put("monthly", statsDAO.monthly());
        stats.put("forever", statsDAO.forever());

        return Response.ok(stats).build();
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.statsDAO = dbi.get().onDemand(TurnoverStatsDAO.class);
    }
}
