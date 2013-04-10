package org.mayocat.shop.checkout.front;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.validator.routines.EmailValidator;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CustomerDetails;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.theme.Breakpoint;
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
    private FrontBindingManager bindingManager;

    @Inject
    private CheckoutRegister checkoutRegister;

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
            Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

            bindings.put("request", data);
            bindings.put("errors", errors);

            result.putBindings(bindings);
            return result;
        }

        CustomerDetails customer = new CustomerDetails(email);

        //checkoutRegister.checkout();

        FrontView result = new FrontView("checkout/success", breakpoint);
        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());
        bindings.put("errors", errors);

        result.putBindings(bindings);
        return result;
    }

    @GET
    public FrontView getCheckoutForm(@Context UriInfo uriInfo, @Context Breakpoint breakpoint)
    {
        FrontView result = new FrontView("checkout/form", breakpoint);
        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        result.putBindings(bindings);
        return result;
    }
}
