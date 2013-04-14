package org.mayocat.shop.billing.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeZone;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.ResultSetRepresentation;
import org.mayocat.shop.billing.api.representation.OrderRepresentation;
import org.mayocat.shop.billing.meta.OrderEntity;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.yammer.metrics.annotation.Timed;

/**
 * @version $Id$
 */
@Component(OrderResource.PATH)
@Path(OrderResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class OrderResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + OrderEntity.PATH;

    @Inject
    private OrderStore orderStore;

    @Inject
    private ConfigurationService configurationService;

    @GET
    @Timed
    @Authorized
    public ResultSetRepresentation<OrderRepresentation> getAllOrders(
            @QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        final DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue());
        List<Order> orders = orderStore.findAll(number, offset);

        Collection<OrderRepresentation> representations =
                Collections2.transform(orders, new Function<Order, OrderRepresentation>()
                {
                    public OrderRepresentation apply(final Order order)
                    {
                        return new OrderRepresentation(tenantTz, order);
                    }
                });

        ResultSetRepresentation<OrderRepresentation> resultSet = new ResultSetRepresentation<OrderRepresentation>(
                PATH,
                number,
                offset,
                new ArrayList<OrderRepresentation>(representations)
        );

        return resultSet;
    }
}
