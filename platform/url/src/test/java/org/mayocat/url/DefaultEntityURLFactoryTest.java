/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.url.testsupport.Employee;
import org.mayocat.url.testsupport.SomeEntity;
import org.mayocat.url.testsupport.SomeEntityURLFactory;
import org.mayocat.url.testsupport.Todo;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
@ComponentList({ SomeEntityURLFactory.class, ComponentManager.class })
public class DefaultEntityURLFactoryTest
{
    private Tenant tenant;

    private SiteSettings siteSettings;

    @Before
    public void setUp() throws Exception
    {
        this.siteSettings = componentManager.getInstance(SiteSettings.class);

        when(siteSettings.getDomainName()).thenReturn("localhost:8080");

        tenant = new Tenant();
        tenant.setSlug("shop");
    }

    @Rule
    public final MockitoComponentMockingRule<EntityURLFactory> componentManager =
            new MockitoComponentMockingRule(DefaultEntityURLFactory.class,
                    Arrays.asList(ComponentManager.class));

    @Test
    public void testCreateURLWithoutCustomEntityURLFactory() throws Exception
    {
        Employee entity = new Employee(UUID.randomUUID(), "john-doe");

        URL url = this.componentManager.getComponentUnderTest().create(entity, tenant);
        Assert.assertEquals("http://shop.localhost:8080/employees/john-doe" , url.toString());
    }

    @Test
    public void testCreateURLWithCustomEntityURLFactory() throws Exception
    {
        SomeEntity entity = new SomeEntity(UUID.randomUUID(), "anything");

        URL url = this.componentManager.getComponentUnderTest().create(entity, tenant);
        Assert.assertEquals("http://perdu.com", url.toString());
    }

    @Test
    public void testCreateURLWithPluralFormAnnotation() throws Exception
    {
        Todo entity = new Todo(UUID.randomUUID(), "write-more-tests");

        URL url = this.componentManager.getComponentUnderTest().create(entity, tenant);
        Assert.assertEquals("http://shop.localhost:8080/todos/write-more-tests", url.toString());
    }
}
