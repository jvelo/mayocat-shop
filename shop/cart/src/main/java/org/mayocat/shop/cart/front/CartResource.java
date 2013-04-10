package org.mayocat.shop.cart.front;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.resources.AbstractFrontResource;
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
public class CartResource extends AbstractFrontResource implements Resource
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private CartAccessor cartAccessor;

    @POST
    @Path("add")
    public Response addToCart(@FormParam("product") String productSlug,
            @FormParam("quantity") @DefaultValue("1") Long quantity, @Context HttpServletRequest request)
            throws URISyntaxException
    {
        if (Strings.isNullOrEmpty(productSlug)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Product product = productStore.get().findBySlug(productSlug);
        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Product not found").build();
        }

        Cart cart = cartAccessor.getCart();
        cart.addItem(product, quantity);

        return Response.seeOther(new URI("/cart")).build();
    }

    @POST
    @Path("update")
    public Response updateCart(MultivaluedMap<String, String> queryParams) throws URISyntaxException
    {
        boolean isRemoveItemRequest = false;
        Cart cart = cartAccessor.getCart();

        for (String key : queryParams.keySet()) {
            if (key.startsWith("remove_")) {
                // Handle "remove product" request
                isRemoveItemRequest = true;
                try {
                    Integer index = Integer.valueOf(key.substring("remove_".length()));
                    Map<Purchasable, Long> items = cart.getItems();
                    Integer loopIndex = 0;
                    Purchasable itemToRemove = null;
                    for (Purchasable purchasable : items.keySet()) {
                        if (loopIndex.equals(index)) {
                            itemToRemove = purchasable;
                        }
                        loopIndex++;
                    }
                    if (itemToRemove != null) {
                        cart.removeItem(itemToRemove);
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                } catch (NumberFormatException e) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
        }

        if (!isRemoveItemRequest) {
            // Handle update request
            for (String key : queryParams.keySet()) {
                if (key.startsWith("quantity_")) {
                    Long quantity = Long.valueOf(queryParams.getFirst(key));
                    try {
                        Integer index = Integer.valueOf(key.substring("quantity_".length()));

                        Map<Purchasable, Long> items = cart.getItems();
                        Integer loopIndex = 0;
                        for (Purchasable purchasable : items.keySet()) {
                            if (loopIndex.equals(index)) {
                                cart.setItem(purchasable, quantity);
                            }
                            loopIndex++;
                        }
                    } catch (NumberFormatException e) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }
            }
        }

        return Response.seeOther(new URI("/cart")).build();
    }

    @GET
    public FrontView getCart(@Context Breakpoint breakpoint, @Context UriInfo uriInfo, @Context Locale locale)
    {
        FrontView result = new FrontView("cart", breakpoint);
        Map<String, Object> context = getContext(uriInfo);
        result.putContext(context);
        return result;
    }
}
