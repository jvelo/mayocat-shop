/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.front.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.cart.front.context.CartContext;
import org.mayocat.shop.cart.front.context.CartItemContext;
import org.mayocat.shop.cart.front.context.DeliveryTimeContext;
import org.mayocat.shop.cart.front.context.ShippingOptionContext;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.front.representation.PriceRepresentation;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ImageContext;
import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.theme.ThemeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class CartContextBuilder
{
    private AttachmentStore attachmentStore;

    private ThumbnailStore thumbnailStore;

    private ImageContextBuilder imageContextBuilder;

    private ShippingService shippingService;

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(CartContextBuilder.class);

    public CartContextBuilder(
            AttachmentStore attachmentStore,
            ThumbnailStore thumbnailStore,
            ShippingService shippingService,
            ThemeDefinition theme)
    {
        this.attachmentStore = attachmentStore;
        this.thumbnailStore = thumbnailStore;
        this.shippingService = shippingService;
        this.imageContextBuilder = new ImageContextBuilder(theme);
    }

    public CartContext build(Cart cart, Locale locale)
    {
        LOGGER.debug("Building cart context...");

        Long numberOfItems = 0l;
        List<CartItemContext> itemsContext = Lists.newArrayList();

        Map<Purchasable, Long> items = cart.getItems();
        PriceRepresentation total = new PriceRepresentation(cart.getTotal(), cart.getCurrency(), locale);

        Collection<UUID> featuredImageIds = Collections2.transform(cart.getItems().keySet(),
                new Function<Purchasable, UUID>()
                {
                    @Override
                    public UUID apply(final Purchasable product)
                    {
                        if (product.getFeaturedImageId() != null) {
                            return product.getFeaturedImageId();
                        }
                        if (product.getParent().isPresent() && product.getParent().get().isLoaded()) {
                            return product.getParent().get().get().getFeaturedImageId();
                        }
                        return null;
                    }
                }
        );
        List<UUID> ids = new ArrayList<>(Collections2.filter(featuredImageIds, Predicates.notNull()));
        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;
        if (ids.isEmpty()) {
            allImages = Collections.emptyList();
            allThumbnails = Collections.emptyList();
        } else {
            allImages = this.attachmentStore.findByIds(ids);
            allThumbnails = this.thumbnailStore.findAllForIds(ids);
        }

        for (final Purchasable purchasable : items.keySet()) {

            LOGGER.debug("Adding purchasable {} to cart context", purchasable.getTitle());

            Long quantity = items.get(purchasable);
            final Purchasable product;
            CartItemContext cir = new CartItemContext();
            BigDecimal price;

            if (purchasable.getParent().isPresent()) {
                if (!purchasable.getParent().get().isLoaded()) {
                    // This should never happen
                    throw new RuntimeException("Can't build cart with a variant which parent product is not loaded");
                }
                product = purchasable.getParent().get().get();
                cir.setVariant(purchasable.getTitle());

                if (purchasable.getUnitPrice() != null) {
                    price = purchasable.getUnitPrice();
                }
                else {
                    price = product.getUnitPrice();
                }
            }
            else {
                product = purchasable;
                price = product.getUnitPrice();
            }

            Collection<Attachment> attachments = Collections2.filter(allImages, new Predicate<Attachment>()
            {
                @Override
                public boolean apply(@Nullable Attachment attachment)
                {
                    if (product.getFeaturedImageId() != null) {
                        return attachment.getId().equals(product.getFeaturedImageId());
                    }
                    if (product.getParent().isPresent() && product.getParent().get().isLoaded()) {
                        return attachment.getId().equals(product.getParent().get().get().getFeaturedImageId());
                    }
                    return false;
                }
            });
            List<Image> images = new ArrayList<Image>();
            for (final Attachment attachment : attachments) {
                Collection<Thumbnail> thumbnails = Collections2.filter(allThumbnails, new Predicate<Thumbnail>()
                {
                    @Override
                    public boolean apply(@Nullable Thumbnail thumbnail)
                    {
                        return thumbnail.getAttachmentId().equals(attachment.getId());
                    }
                });
                Image image = new Image(attachment, new ArrayList<Thumbnail>(thumbnails));
                images.add(image);
            }

            cir.setTitle(product.getTitle());
            cir.setDescription(product.getDescription());
            cir.setQuantity(quantity);

            if (Product.class.isAssignableFrom(product.getClass())) {
                cir.setType("product");
                cir.setSlug(((Product) product).getSlug());
            } else {
                cir.setType(product.getClass().getSimpleName().toLowerCase());
            }

            cir.setId(product.getId());

            if (images.size() > 0) {
                ImageContext featuredImageContext = imageContextBuilder.createImageContext(images.get(0));
                cir.setFeaturedImage(featuredImageContext);
            }

            PriceRepresentation unitPrice =
                    new PriceRepresentation(price, cart.getCurrency(), locale);
            PriceRepresentation itemTotal =
                    new PriceRepresentation(cart.getItemTotal(purchasable), cart.getCurrency(), locale);

            cir.setUnitPrice(unitPrice);
            cir.setItemTotal(itemTotal);

            numberOfItems += quantity;
            itemsContext.add(cir);
        }

        PriceRepresentation itemsTotal =
                new PriceRepresentation(cart.getItemsTotal(), cart.getCurrency(), locale);

        CartContext context = new CartContext(itemsContext, numberOfItems, itemsTotal, total);
        context.setHasShipping(shippingService.isShippingEnabled());

        if (context.isHasShipping()) {
            PriceRepresentation shippingPrice = new PriceRepresentation(
                    cart.getSelectedShippingOption() != null ? cart.getSelectedShippingOption().getPrice() : BigDecimal.ZERO,
                    cart.getCurrency(), locale);

            if (cart.getSelectedShippingOption() != null) {
                context.setSelectedShippingOption(buildOption(cart.getSelectedShippingOption(), cart, locale, true));
            }

            List<ShippingOptionContext> availableOptionsContext = new ArrayList<ShippingOptionContext>();
            List<ShippingOption> availableOptions = shippingService.getOptions(cart.getItems());
            for (ShippingOption option : availableOptions) {
                availableOptionsContext.add(buildOption(option, cart, locale, cart.getSelectedShippingOption() != null &&
                        cart.getSelectedShippingOption().getCarrierId().equals(option.getCarrierId())));
            }
            context.setShippingOptions(availableOptionsContext);
            context.setShipping(shippingPrice);
        }

        return context;
    }

    private ShippingOptionContext buildOption(ShippingOption option, Cart cart, Locale locale, boolean selected)
    {
        PriceRepresentation optionPrice =
                new PriceRepresentation(option.getPrice(), cart.getCurrency(), locale);
        ShippingOptionContext context =
                new ShippingOptionContext(option.getCarrierId(), option.getTitle(), optionPrice);
        Carrier carrier = shippingService.getCarrier(option.getCarrierId());
        if (carrier != null) {
            context.setDeliveryTime(new DeliveryTimeContext(carrier.getMinimumDays(), carrier.getMaximumDays()));
            context.setDestinations(shippingService.getDestinationNames(carrier.getDestinations()));
            context.setSelected(selected);
        }
        return context;
    }
}
