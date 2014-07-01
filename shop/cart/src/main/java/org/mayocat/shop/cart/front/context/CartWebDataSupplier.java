/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.front.context;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.front.builder.CartContextBuilder;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.attachment.store.AttachmentStore;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("cart")
public class CartWebDataSupplier implements WebDataSupplier
{
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

    @Override
    public void supply(Map<String, Object> data)
    {
        if (data.containsKey("cart")) {
            // If the cart data has already been supplied (because for example this is a cart request), we pass
            return;
        }

        CartContextBuilder builder = new CartContextBuilder(attachmentStore.get(), thumbnailStore.get(),
                shippingService, context.getTheme().getDefinition());

        final Locale locale = generalSettings.getLocales().getMainLocale().getValue();
        data.put("cart", builder.build(cartAccessor.getCart(), locale));
    }
}
