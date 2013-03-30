package org.mayocat.shop.cart.front;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.cart.model.CartItem;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

/**
 * @version $Id$
 */
@Component("/cart")
@Path("/cart")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CartResource implements Resource
{
    public static final String SESSION_CART_KEY = "org.mayocat.shop.cart.front.Cart";

    @Inject
    private CatalogService catalogService;

    @Inject
    private FrontBindingManager bindingManager;

    @POST
    public Response addToCart(@FormParam("product") String productSlug,
            @FormParam("quantity") @DefaultValue("1") Integer quantity, @Context HttpServletRequest request)
    {
        if (Strings.isNullOrEmpty(productSlug)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Product product = catalogService.findProductBySlug(productSlug);
        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Product not found").build();
        }

        Cart cart = getCartFromRequest(request);
        CartItem newItem = new CartItem(product, quantity);
        cart.addItem(newItem);

        return Response.ok().build();
    }

    @GET
    public FrontView getCart(@Context HttpServletRequest request, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        Cart cart = getCartFromRequest(request);

        FrontView result = new FrontView("home", breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());
        bindings.put("cart", cart);

        result.putBindings(bindings);

        return result;
    }

    private Cart getCartFromRequest(HttpServletRequest request)
    {
        if (getSession(request).getAttribute(SESSION_CART_KEY) != null) {
            return (Cart) getSession(request).getAttribute(SESSION_CART_KEY);
        }
        Cart cart = new Cart();
        getSession(request).setAttribute(SESSION_CART_KEY, cart);
        return cart;
    }

    private HttpSession getSession(HttpServletRequest request)
    {
        return request.getSession(true);
    }
}
