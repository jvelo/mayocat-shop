package org.mayocat.shop.payment.api.resources;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.PaymentException;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.PaymentResponse;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.ObservationManager;

/**
 * @version $Id$
 */
@Component(PaymentResource.PATH)
@Path(PaymentResource.PATH)
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
@ExistingTenant
public class PaymentResource implements Resource
{
    public static final String PATH = "payment";

    public static final String ACKNOWLEDGEMENT_PATH = "acknowledgement";

    @Inject
    private Map<String, GatewayFactory> gatewayFactories;

    @Inject
    private Provider<PaymentOperationStore> paymentOperationStore;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private Logger logger;

    @POST
    @Path("{orderId}/" + ACKNOWLEDGEMENT_PATH + "/{gatewayId}")
    public Response acknowledgePayment(@PathParam("gatewayId") String gatewayId, @PathParam("orderId") Long orderId,
            MultivaluedMap<String, String> data)
    {
        GatewayFactory factory = gatewayFactories.get(gatewayId);
        PaymentGateway gateway = factory.createGateway();

        try {
            PaymentResponse response = gateway.acknowledge(data);
            PaymentOperation op = response.getOperation();
            op.setOrderId(orderId);
            paymentOperationStore.get().create(op);

            observationManager.notify(new PaymentOperationEvent(), op);
        } catch (PaymentException e) {
            this.logger.error("Failed to acknowledge payment", e);
        } catch (InvalidEntityException e) {
            this.logger.error("Failed to acknowledge payment", e);
        } catch (EntityAlreadyExistsException e) {
            this.logger.error("Failed to acknowledge payment", e);
        }

        return Response.ok().build();
    }
}
