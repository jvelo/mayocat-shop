/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.cart.model.CartInSession;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.shipping.ShippingOption;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
public class DefaultCartInSessionConverterTest
{
    @Rule
    public final MockitoComponentMockingRule<DefaultCartInSessionConverter> componentManager =
            new MockitoComponentMockingRule(DefaultCartInSessionConverter.class);

    private Product p1;
    private Product p2;

    @Before
    public void configure() throws Exception
    {
        final ProductStore productStore = this.componentManager.getInstance(ProductStore.class);

        UUID id1 = UUID.randomUUID();
        p1 = new Product();
        p1.setId(id1);
        p1.setPrice(BigDecimal.ONE);

        UUID id2 = UUID.randomUUID();
        p2 = new Product();
        p2.setId(id2);
        p2.setPrice(BigDecimal.TEN);

        when(productStore.findById(id1)).thenReturn(p1);
        when(productStore.findById(id2)).thenReturn(p2);
    }

    @Test
    public void testConversion() throws ComponentLookupException
    {
        Cart cart = new Cart(Currency.getInstance("EUR"));
        cart.addItem(p1, 4l);
        cart.addItem(p2, 3l);

        ShippingOption shippingOption =
                new ShippingOption(UUID.randomUUID(), "International Space Station", BigDecimal.ONE);
        cart.setSelectedShippingOption(shippingOption);

        // Do the Cart ))<>(( CartInSession back and forth

        CartInSession inSession = componentManager.getComponentUnderTest().convertToCartInSession(cart);
        Assert.assertEquals(2, inSession.getItems().size());

        Cart retrieved = componentManager.getComponentUnderTest().loadFromCartInSession(inSession);

        Assert.assertEquals(BigDecimal.valueOf(34), retrieved.getItemsTotal());
        Assert.assertEquals(BigDecimal.valueOf(35), retrieved.getTotal());

        Assert.assertEquals(cart, retrieved);
    }
}
