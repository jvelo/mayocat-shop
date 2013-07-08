package org.mayocat.shop.payment.api.resources;

import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.payment.GatewayException;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.GatewayResponse;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.ObservationManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

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
    public Response acknowledgePayment(@PathParam("gatewayId") String gatewayId, @PathParam("orderId") UUID orderId,
            MultivaluedMap<String, String> data)
    {
        GatewayFactory factory = gatewayFactories.get(gatewayId);
        PaymentGateway gateway = factory.createGateway();
        String redirect = "";

        try {
            GatewayResponse response = gateway.acknowledge(data);
            PaymentOperation op = response.getOperation();
            op.setOrderId(orderId);
            paymentOperationStore.get().create(op);

            observationManager.notify(new PaymentOperationEvent(), op);

            if(gatewayId.equalsIgnoreCase("monetawebadaptivepayments"))  {
                redirect = "redirect=" + response.getRedirectURL();
            }

        } catch (GatewayException e) {
            this.logger.error("Failed to acknowledge payment", e);
        } catch (InvalidEntityException e) {
            this.logger.error("Failed to acknowledge payment", e);
        } catch (EntityAlreadyExistsException e) {
            this.logger.error("Failed to acknowledge payment", e);
        }

        return Response.ok(redirect).build();
    }
}
