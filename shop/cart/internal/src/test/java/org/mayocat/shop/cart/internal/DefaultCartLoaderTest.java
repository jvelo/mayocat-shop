/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import java.io.IOException;
import java.util.Currency;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.WebContext;
import org.mayocat.shop.cart.Cart;
import org.mayocat.shop.cart.CartContents;
import org.mayocat.shop.cart.CartLoader;
import org.mayocat.shop.cart.internal.support.CartTest;
import org.mayocat.shop.cart.internal.support.CartTestDefinition;
import org.mayocat.shop.cart.internal.support.InputItem;
import org.mayocat.shop.cart.internal.support.ItemExpectation;
import org.mayocat.shop.cart.internal.support.TestExpectation;
import org.mayocat.shop.cart.internal.support.TestInput;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.taxes.PriceWithTaxes;
import org.mayocat.shop.taxes.TaxesService;
import org.mayocat.shop.taxes.configuration.TaxesSettings;
import org.mayocat.shop.taxes.internal.DefaultTaxesService;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentManagerRule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
@ComponentList({ DefaultTaxesService.class, DefaultCartLoader.class })
public class DefaultCartLoaderTest
{
    private CartContents cartContents;

    @Rule
    public MockitoComponentManagerRule componentManager = new MockitoComponentManagerRule();

    @Before
    public void setUp() throws Exception
    {
        this.componentManager.registerMockComponent(ConfigurationService.class);
        this.componentManager.registerMockComponent(WebContext.class);
        this.componentManager.registerMockComponent(TenantStore.class);
    }

    @Test
    public void executeTests() throws Exception
    {
        runTest("no-taxes.yml");
        runTest("simple-vat.yml");
    }

    private void runTest(String name) throws Exception
    {
        cartContents = new CartContents(Currency.getInstance("EUR"));

        CartTestDefinition definition = loadTest(name);

        // Mock settings
        TaxesService taxesService = componentManager.getInstance(TaxesService.class);
        ConfigurationService configurationService = componentManager.getInstance(ConfigurationService.class);
        TaxesSettings settings = definition.getTaxesSettings();
        if (settings == null) {
            settings = new TaxesSettings();
        }
        when(configurationService.getSettings(TaxesSettings.class)).thenReturn(settings);

        // Mock web context
        WebContext webContext = componentManager.getInstance(WebContext.class);
        when(webContext.getTenant()).thenReturn(null);

        // Mock tenant store
        TenantStore tenantStore = componentManager.getInstance(TenantStore.class);
        when(tenantStore.findById(any(UUID.class))).thenReturn(new Tenant());

        CartTest test = definition.getTest();
        TestInput input = test.getInput();
        TestExpectation expectation = test.getExpected();

        for (InputItem item : input.getItems()) {
            Product p = new Product();
            p.setId(UUID.randomUUID());
            p.setPrice(item.getUnitPrice());

            cartContents.addItem(p, item.getQuantity());
        }

        CartLoader cartLoader = componentManager.getInstance(CartLoader.class);

        Cart cart = cartLoader.load(cartContents);

        assertEquivalent(expectation.getItemsTotal(), cart.itemsTotal());

        Assert.assertEquals(expectation.getItems().size(), cart.items().size());

        Integer i = 0;
        for (ItemExpectation itemExpectation : expectation.getItems()) {
            assertEquivalent(itemExpectation.getUnitPrice(), cart.items().get(i).unitPrice());
            assertEquivalent(itemExpectation.getTotal(), cart.items().get(i).total());
            i++;
        }
    }

    private void assertEquivalent(PriceWithTaxes p1, PriceWithTaxes p2)
    {
        Assert.assertTrue(p1.incl().compareTo(p2.incl()) == 0);
        Assert.assertTrue(p1.excl().compareTo(p2.excl()) == 0);
        Assert.assertTrue(p1.vat().compareTo(p2.vat()) == 0);
    }

    private CartTestDefinition loadTest(String name) throws IOException
    {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String test = Resources.toString(Resources.getResource("tests/" + name), Charsets.UTF_8);

        return mapper.readValue(test, CartTestDefinition.class);
    }
}
