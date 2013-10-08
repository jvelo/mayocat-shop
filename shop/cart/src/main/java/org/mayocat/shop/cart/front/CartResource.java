package org.mayocat.shop.cart.front;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
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
import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

/**
 * One important rule is  : every time the cart state is modified, CartAccessor#setCart MUST be called, in order
 * for the cart serialization in the session (cookies) to be updated.
 *
 * @version $Id$
 */
@Component("/cart")
@Path("/cart")
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CartResource extends AbstractFrontResource implements Resource
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private ShippingService shippingService;

    @POST
    @Path("add")
    public Response addToCart(@FormParam("product") String productSlug,
            @FormParam("quantity") @DefaultValue("1") Long quantity)
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

        cartAccessor.setCart(cart);

        recalculateShipping();

        return Response.seeOther(new URI("/cart")).build();
    }

    @POST
    @Path("remove")
    public Response removeFromCart(@FormParam("product") String productSlug)
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
        cart.removeItem(product);

        recalculateShipping();

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
                    try {
                        Long quantity = Long.valueOf(queryParams.getFirst(key));
                        Integer index = Integer.valueOf(key.substring("quantity_".length()));

                        Map<Purchasable, Long> items = cart.getItems();
                        Integer loopIndex = 0;
                        Purchasable toRemove = null;
                        for (Purchasable purchasable : items.keySet()) {
                            if (loopIndex.equals(index)) {
                                if (quantity <= 0) {
                                    toRemove = purchasable;
                                } else {
                                    cart.setItem(purchasable, quantity);
                                }
                            }
                            loopIndex++;
                        }
                        if (toRemove != null) {
                            cart.removeItem(toRemove);
                        }
                    } catch (NumberFormatException e) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }
            }
        }

        if (queryParams.getFirst("shipping_option") != null) {
            UUID carrierId = UUID.fromString(queryParams.getFirst("shipping_option"));
            ShippingOption option = shippingService.getOption(carrierId, cart.getItems());
            cart.setSelectedShippingOption(option);
        }

        cartAccessor.setCart(cart);

        recalculateShipping();

        return Response.seeOther(new URI("/cart")).build();
    }

    @GET
    @Produces("application/json")
    public Map<String, Object> getCartContext(@Context UriInfo uriInfo)
    {
        Cart cart = cartAccessor.getCart();
        if (!cart.isEmpty() && shippingService.isShippingEnabled() && cart.getSelectedShippingOption() == null) {
            List<ShippingOption> options = shippingService.getOptions(cart.getItems());
            if (!options.isEmpty()) {
                cart.setSelectedShippingOption(options.get(0));
                cartAccessor.setCart(cart);
            }
        }
        Map<String, Object> context = getContext(uriInfo);
        return context;
    }

    @GET
    @Produces("text/html;q=2")
    public FrontView getCart(@Context Breakpoint breakpoint, @Context UriInfo uriInfo, @Context Locale locale)
    {
        FrontView result = new FrontView("cart", breakpoint);
        result.putContext(getCartContext(uriInfo));
        return result;
    }

    private void recalculateShipping()
    {
        Cart cart = cartAccessor.getCart();

        // In case shipping has been disabled or cart emptied
        if (!shippingService.isShippingEnabled() || cartIsEmpty(cart)) {
            cart.setSelectedShippingOption(null);
            cartAccessor.setCart(cart);
            return;
        }

        if (cart.getSelectedShippingOption() == null) {
            // Nothing else to do if we get there
            return;
        }

        UUID selectedCarrierId = cart.getSelectedShippingOption().getCarrierId();
        cart.setSelectedShippingOption(shippingService.getOption(selectedCarrierId, cart.getItems()));

        cartAccessor.setCart(cart);
    }

    private boolean cartIsEmpty(Cart cart)
    {
        for (Long i : cart.getItems().values()) {
            if (i > 0) {
                return false;
            }
        }
        return true;
    }
}
