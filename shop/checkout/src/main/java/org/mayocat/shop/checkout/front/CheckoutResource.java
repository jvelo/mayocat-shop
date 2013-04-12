package org.mayocat.shop.checkout.front;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.validator.routines.EmailValidator;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.store.AddressStore;
import org.mayocat.shop.billing.store.CustomerStore;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CheckoutResponse;
import org.mayocat.shop.checkout.CustomerDetails;
import org.mayocat.shop.front.FrontContextManager;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.theme.Breakpoint;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component(CheckoutResource.PATH)
@Path(CheckoutResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CheckoutResource implements Resource
{
    public static final String PATH = "checkout";

    @Inject
    private FrontContextManager contextManager;

    @Inject
    private CheckoutRegister checkoutRegister;

    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private Logger logger;

    private enum ErrorType
    {
        REQUIRED,
        BAD_VALUE;

        @JsonValue
        public String toJson()
        {
            return name().toLowerCase();
        }
    }

    private class Error
    {
        private String userMessage;

        private ErrorType errorType;

        public Error(ErrorType type, String userMessage)
        {
            this.errorType = type;
            this.userMessage = userMessage;
        }

        public String getUserMessage()
        {
            return userMessage;
        }

        public ErrorType getErrorType()
        {
            return errorType;
        }
    }

    @POST
    public FrontView checkout(@Context UriInfo uriInfo, @Context Breakpoint breakpoint, MultivaluedMap data)
    {
        Map<String, Error> errors = Maps.newHashMap();
        String email = null;

        if (data.containsKey("email")) {
            email = (String) data.getFirst("email");
            EmailValidator emailValidator = EmailValidator.getInstance(false);
            if (!emailValidator.isValid(email)) {
                Error error = new Error(ErrorType.BAD_VALUE, "email is not valid");
                errors.put("email", error);
            }
        } else {
            Error error = new Error(ErrorType.REQUIRED, "email is mandatory");
            errors.put("email", error);
        }

        if (errors.keySet().size() > 0) {
            FrontView result = new FrontView("checkout/form", breakpoint);
            Map<String, Object> bindings = contextManager.getContext(uriInfo);

            bindings.put("request", data);
            bindings.put("errors", errors);

            result.putContext(bindings);
            return result;
        }

        Customer customer = new Customer();
        customer.setEmail(email);

        try {
            Cart cart = cartAccessor.getCart();
            checkoutRegister.checkout(cart, uriInfo, customer, null, null);

            FrontView result = new FrontView("checkout/success", breakpoint);
            Map<String, Object> bindings = contextManager.getContext(uriInfo);
            bindings.put("errors", errors);

            result.putContext(bindings);
            return result;
        } catch (final Exception e) {
            this.logger.error("Exception checking out", e);
            FrontView result = new FrontView("checkout/exception", breakpoint);
            Map<String, Object> bindings = contextManager.getContext(uriInfo);
            bindings.put("exception", new HashMap<String, Object>()
            {
                {
                    put("message", e.getMessage());
                }
            });
            result.putContext(bindings);
            return result;
        }
    }

    @GET
    public Object checkout(@Context UriInfo uriInfo, @Context Breakpoint breakpoint)
    {
        if (checkoutRegister.requiresForm()) {
            FrontView result = new FrontView("checkout/form", breakpoint);
            Map<String, Object> bindings = contextManager.getContext(uriInfo);

            result.putContext(bindings);
            return result;
        } else {
            try {
                Cart cart = cartAccessor.getCart();
                CheckoutResponse response = checkoutRegister.checkout(cart, uriInfo, null, null, null);

                if (response.getRedirectURL().isPresent()) {
                    return Response.seeOther(new URI(response.getRedirectURL().get())).build();
                }
            } catch (final CheckoutException e) {
                FrontView result = new FrontView("checkout/exception", breakpoint);
                Map<String, Object> bindings = contextManager.getContext(uriInfo);
                bindings.put("exception", new HashMap<String, Object>()
                {
                    {
                        put("message", e.getMessage());
                    }
                });
                result.putContext(bindings);
                return result;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return Response.ok().build();
    }

    @GET
    @Path("payment/return")
    public FrontView returnFromExternalPaymentService(@Context UriInfo uriInfo, @Context Breakpoint breakpoint)
    {
        for (String key : uriInfo.getQueryParameters().keySet()) {
            System.out.println(key + " : " + uriInfo.getQueryParameters().getFirst(key));
        }

        FrontView result = new FrontView("checkout/success", breakpoint);
        Map<String, Object> bindings = contextManager.getContext(uriInfo);

        result.putContext(bindings);
        return result;
    }

    @GET
    @Path("payment/cancel")
    public FrontView cancelFromExternalPaymentService(@Context UriInfo uriInfo, @Context Breakpoint breakpoint)
    {
        FrontView result = new FrontView("checkout/cancelled", breakpoint);
        Map<String, Object> bindings = contextManager.getContext(uriInfo);

        result.putContext(bindings);
        return result;
    }
}
