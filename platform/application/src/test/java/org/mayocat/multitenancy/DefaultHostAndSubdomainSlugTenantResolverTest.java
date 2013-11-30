/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.multitenancy;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.AccountsService;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.test.jmock.AbstractMockingComponentTestCase;
import org.xwiki.test.jmock.annotation.MockingRequirement;

@MockingRequirement(value = DefaultHostAndSubdomainSlugTenantResolver.class)
public class DefaultHostAndSubdomainSlugTenantResolverTest extends AbstractMockingComponentTestCase<TenantResolver>
{
    private TenantResolver tenantResolver;

    private MultitenancySettings configuration;

    private AccountsService accountsService;

    /**
     * Setup mock dependencies before initializing the @MockingRequirement components.
     */
    public void setupDependencies() throws Exception
    {
        // Allow to mock classes for mocking configuration instances
        getMockery().setImposteriser(ClassImposteriser.INSTANCE);

        configuration = getMockery().mock(MultitenancySettings.class);
 
        DefaultComponentDescriptor<MultitenancySettings> cd =
            new DefaultComponentDescriptor<MultitenancySettings>();
        cd.setRoleType(MultitenancySettings.class);
        this.getComponentManager().registerComponent(cd, this.configuration);
    }

    @Before
    @Override
    public void setUp() throws Exception
    {
        getMockery().setImposteriser(ClassImposteriser.INSTANCE);
        //this.setupDependencies();

        super.setUp();

        configuration = this.getComponentManager().getInstance(MultitenancySettings.class);
        accountsService = this.getComponentManager().getInstance(AccountsService.class);
        tenantResolver = getMockedComponent();

        getMockery().checking(new Expectations()
        {
            {
                allowing(configuration).getDefaultTenantSlug();
                will(returnValue("mytenant"));

                allowing(accountsService).findTenant(with(Matchers.not(equal("mytenant"))));
                will(returnValue(null));

                allowing(accountsService).findTenant(with(equal("mytenant")));
                will(returnValue(new Tenant("mytenant", null)));

                allowing(accountsService).createTenant(with(any(Tenant.class)));

            }
        });
    }

    @Test
    public void testMultitenancyNotActivatedReturnsDefaultTenant1() throws Exception
    {
        this.setUpExpectationsForMultitenancyNotActivated();
        assertNotNull(this.tenantResolver.resolve("mayocatshop.com"));
        assertEquals("mytenant", this.tenantResolver.resolve("mayocatshop.com").getSlug());
    }

    @Test
    public void testMultitenancyNotActivatedReturnsDefaultTenant2() throws Exception
    {
        this.setUpExpectationsForMultitenancyNotActivated();
        assertNotNull(this.tenantResolver.resolve("localhost"));
        assertEquals("mytenant", this.tenantResolver.resolve("localhost").getSlug());
    }

    @Test
    public void testMultitenancyTenantResolver1() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        assertNotNull(this.tenantResolver.resolve("mytenant.mayocatshop.com"));
        assertEquals("mytenant", this.tenantResolver.resolve("mytenant.mayocatshop.com").getSlug());
    }

    @Test
    public void testMultitenancyTenantResolver2() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        assertNotNull(this.tenantResolver.resolve("mytenant.localhost"));
        assertEquals("mytenant", this.tenantResolver.resolve("mytenant.localhost").getSlug());
    }

    @Test
    public void testMultitenancyTenantResolver3() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        assertNull(this.tenantResolver.resolve("localhost"));
    }

    @Test
    public void testMultitenancyTenantResolver4() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        assertNull(this.tenantResolver.resolve("mayocatshop.com"));
    }

    @Test
    public void testMultitenancyTenantResolver5() throws Exception
    {
        //TODO
        // Test IP addresses resolving.
        // (right now it throws a illegal argument exception
    }


    // ///////////////////////////////////////////////////////////////////////////////////

    private void setUpExpectationsForMultitenancyActivated()
    {
        getMockery().checking(new Expectations()
        {
            {
                allowing(configuration).isActivated();
                will(returnValue(true));
            }
        });
    }

    private void setUpExpectationsForMultitenancyNotActivated()
    {
        getMockery().checking(new Expectations()
        {
            {
                allowing(configuration).isActivated();
                will(returnValue(false));

                allowing(configuration).getDefaultTenantSlug();
                will(returnValue("mytenant"));
            }
        });
    }
}
