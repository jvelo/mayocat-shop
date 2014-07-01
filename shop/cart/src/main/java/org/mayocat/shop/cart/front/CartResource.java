/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.front;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.cart.front.builder.CartContextBuilder;
import org.mayocat.shop.front.resources.AbstractWebViewResource;
import org.mayocat.shop.front.views.WebView;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.attachment.store.AttachmentStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

/**
 * One important rule is  : every time the cart state is modified, CartAccessor#setCart MUST be called, in order for the
 * cart serialization in the session (cookies) to be updated.
 *
 * @version $Id$
 */
@Component("/cart")
@Path("/cart")
@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CartResource extends AbstractWebViewResource implements Resource
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private ShippingService shippingService;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private WebContext context;

    @POST
    @Path("add")
    public Response addToCart(@FormParam("product") String productSlug, @FormParam("variant") String variantSlug,
            @FormParam("quantity") @DefaultValue("1") Long quantity)
            throws URISyntaxException
    {
        if (Strings.isNullOrEmpty(productSlug)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Product product = productStore.get().findBySlug(productSlug);
        if (!Strings.isNullOrEmpty(variantSlug)) {
            Product variant = productStore.get().findVariant(product, variantSlug);
            if (variant == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            variant.setParent(product);
            product = variant;
        }

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
    public WebView getCart()
    {
        Cart cart = cartAccessor.getCart();
        if (!cart.isEmpty() && shippingService.isShippingEnabled() && cart.getSelectedShippingOption() == null) {
            List<ShippingOption> options = shippingService.getOptions(cart.getItems());
            if (!options.isEmpty()) {
                cart.setSelectedShippingOption(options.get(0));
                cartAccessor.setCart(cart);
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        CartContextBuilder builder = new CartContextBuilder(attachmentStore.get(), thumbnailStore.get(),
                shippingService, context.getTheme().getDefinition());

        final Locale locale = generalSettings.getLocales().getMainLocale().getValue();
        data.put("cart", builder.build(cartAccessor.getCart(), locale));

        return new WebView().template("cart.html").data(data);
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
