/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.delegate

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.accounts.store.TenantStore
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.model.Attachment
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartItem
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.model.Purchasable
import org.mayocat.shop.marketplace.web.object.MarketplaceCartItemWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceCartWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceShopWebObject
import org.mayocat.shop.shipping.ShippingService

import javax.inject.Inject
import javax.inject.Provider

/**
 * @version $Id$
 */
@CompileStatic
trait WithMarketplaceCartWebObjectBuilder extends WithProductWebObjectBuilder
{
    @Inject
    ShippingService shippingService
    @Inject
    PlatformSettings platformSettings
    @Inject
    GeneralSettings generalSettings
    @Inject
    Provider<AttachmentStore> attachmentStore
    @Inject
    Provider<ThumbnailStore> thumbnailStore
    @Inject
    Provider<TenantStore> tenantStore
    @Inject
    EntityDataLoader dataLoader

    MarketplaceCartWebObject buildCartWebObject(Cart cart)
    {
        final Locale locale = generalSettings.locales.mainLocale.value

        List<UUID> featuredImageIds = cart.items()
                .collect({ CartItem item ->
                    UUID featuredImageId
                    if (item.item().parent.isPresent() && item.item().parent.get().isLoaded()) {
                        featuredImageId = item.item().featuredImageId ?: item.item().parent.get().get().featuredImageId
                    } else {
                        featuredImageId = item.item().featuredImageId
                    }
                    featuredImageId
                })
                .findAll({ UUID id -> id != null }) as List<UUID>

        List<Attachment> attachments =
                featuredImageIds.isEmpty() ? [] as List<Attachment> : attachmentStore.get().findByIds(featuredImageIds)
        List<Thumbnail> thumbnails =
                featuredImageIds.isEmpty() ? [] as List<Thumbnail> :
                        thumbnailStore.get().findAllForIds(featuredImageIds)

        List<Image> images = attachments.collect({ Attachment attachment ->
            List<Thumbnail> thumbs = thumbnails.
                    findAll({ Thumbnail thumbnail -> thumbnail.attachmentId == attachment.id }) as List<Thumbnail>
            new Image(attachment, thumbs)
        })

        MarketplaceCartWebObject cartWebObject = new MarketplaceCartWebObject()
        cartWebObject.withCart(shippingService, cart, locale, images, platformSettings, Optional.absent())

        List<Product> products = cart.items().collect({ CartItem cartItem ->
            Product product = (cartItem.item().class.isAssignableFrom(Product.class) ? cartItem.item() :
                    null) as Product
            if (product) {
                if (product.parent.isPresent()) {
                    if (!product.parent.get().isLoaded()) {
                        // This should never happen
                        throw new RuntimeException("Can't build cart with a variant which parent product is not loaded")
                    }
                    product = product.parent.get().get() as Product
                } else {
                    product = product
                }
            }
            product
        }).findAll({ Product product -> product != null }) as List<Product>

        List<EntityData<Product>> productsData = dataLoader.
                load(products, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        for (CartItem cartItem : cart.items()) {
            EntityData<Product> entityData = productsData.find({ EntityData<Product> productData ->
                productData.entity.id == cartItem.item().id ||
                        (cartItem.item().parent.isPresent()
                                && productData.entity.id == cartItem.item().parent.get().get().id)
            })

            this.addCartItemToCartWebObject(cart, locale, images, cartItem, entityData, cartWebObject)
        }

        cartWebObject
    }

    def addCartItemToCartWebObject(Cart cart, Locale locale, List<Image> images, CartItem cartItem,
            EntityData<Product> productData, MarketplaceCartWebObject cartWebObject)
    {
        MarketplaceCartItemWebObject cartItemWebObject = new MarketplaceCartItemWebObject()
        Long quantity = cartItem.quantity()
        Purchasable purchasable = cartItem.item()
        Purchasable product

        if (purchasable.parent.isPresent()) {
            if (!purchasable.parent.get().isLoaded()) {
                // This should never happen
                throw new RuntimeException("Can't build cart with a variant which parent product is not loaded")
            }
            product = purchasable.parent.get().get()
            cartItemWebObject.variant = purchasable.title
            cartItemWebObject.variantSlug = purchasable.slug
        } else {
            product = purchasable
        }

        cartItemWebObject.withPurchasable(product, quantity)
        cartItemWebObject.withUnitPrice(cartItem.unitPrice(), cart.currency(), locale)
        cartItemWebObject.withItemTotal(cartItem.total(), cart.currency(), locale)

        Image featuredImage = images.find({ Image image ->
            if (purchasable.parent.isPresent()) {
                return image.attachment.parentId == (cartItem.item().parent.get().get().id ?: cartItem.item().id)
            }
            image.attachment.parentId == cartItem.item().id
        })
        if (featuredImage) {
            cartItemWebObject.withFeaturedImage(cartItem.tenant(), featuredImage, platformSettings)
        }

        if (productData) {
            Tenant tenant = this.tenantStore.get().findById(productData.entity.tenantId)
            cartItemWebObject.product = this.buildProductWebObject(tenant, productData)
            cartItemWebObject.shop = new MarketplaceShopWebObject().withTenant(tenant)
        }

        cartWebObject.items << cartItemWebObject
        cartWebObject.numberOfItems += quantity
    }
}
