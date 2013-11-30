/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.front.context;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.front.builder.CartContextBuilder;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.front.FrontContextSupplier;
import org.mayocat.shop.front.annotation.FrontContext;
import org.mayocat.shop.front.annotation.FrontContextContributor;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.store.AttachmentStore;
import org.mayocat.util.Utils;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("cart")
public class CartContextSupplier implements FrontContextSupplier
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
    private WebContext context;

    @FrontContextContributor(path = "/")
    public void contributeRootContext(@FrontContext Map data)
    {
        CartContextBuilder builder = new CartContextBuilder(attachmentStore.get(), thumbnailStore.get(),
                shippingService, context.getTheme().getDefinition());

        // TODO we need to find a way to have Jersey @Context injection in context suppliers...
        // so that we could here for example get the request locale via @Context Locale locale

        GeneralSettings generalSettings = Utils.getComponent(GeneralSettings.class);
        CatalogSettings catalogSettings = Utils.getComponent(CatalogSettings.class);
        final Locale locale = generalSettings.getLocales().getMainLocale().getValue();
        final Currency currency = catalogSettings.getCurrencies().getMainCurrency().getValue();

        data.put("cart", builder.build(cartAccessor.getCart(), locale));
    }
}
