package org.mayocat.shop.multitenancy;

import junit.framework.Assert;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.configuration.MultitenancyConfiguration;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.service.AccountsService;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.test.AbstractMockingComponentTestCase;
import org.xwiki.test.annotation.MockingRequirement;

public class DefaultTenantResolverTest extends AbstractMockingComponentTestCase
{

    @MockingRequirement(exceptions = MultitenancyConfiguration.class)
    private DefaultTenantResolver tenantResolver;

    private MultitenancyConfiguration configuration;

    private AccountsService accountsService;

    /**
     * Setup mock dependencies before initializing the @MockingRequirement components.
     */
    @Override
    protected void setupDependencies() throws Exception
    {
        // Allow to mock classes for mocking configuration instances
        getMockery().setImposteriser(ClassImposteriser.INSTANCE);

        configuration = getMockery().mock(MultitenancyConfiguration.class);
 
        DefaultComponentDescriptor<MultitenancyConfiguration> cd =
            new DefaultComponentDescriptor<MultitenancyConfiguration>();
        cd.setRoleType(MultitenancyConfiguration.class);
        this.getComponentManager().registerComponent(cd, this.configuration);
    }

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        configuration = this.getComponentManager().getInstance(MultitenancyConfiguration.class);
        accountsService = this.getComponentManager().getInstance(AccountsService.class);

        getMockery().checking(new Expectations()
        {
            {
                allowing(configuration).getDefaultTenant();
                will(returnValue("mytenant"));

                allowing(configuration).getRootDomain();
                will(returnValue(null));

                allowing(accountsService).findTenant(with(Matchers.not(equal("mytenant"))));
                will(returnValue(null));

                allowing(accountsService).findTenant(with(equal("mytenant")));
                will(returnValue(new Tenant("mytenant")));

                allowing(accountsService).createTenant(with(any(Tenant.class)));

            }
        });
    }

    @Test
    public void testMultitenancyNotActivatedReturnsDefaultTenant1() throws Exception
    {
        this.setUpExpectationsForMultitenancyNotActivated();
        Assert.assertNotNull(this.tenantResolver.resolve("mayocatshop.com"));
        Assert.assertEquals("mytenant", this.tenantResolver.resolve("mayocatshop.com").getSlug());
    }

    @Test
    public void testMultitenancyNotActivatedReturnsDefaultTenant2() throws Exception
    {
        this.setUpExpectationsForMultitenancyNotActivated();
        Assert.assertNotNull(this.tenantResolver.resolve("localhost"));
        Assert.assertEquals("mytenant", this.tenantResolver.resolve("localhost").getSlug());
    }

    @Test
    public void testMultitenancyTenantResolver1() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        Assert.assertNotNull(this.tenantResolver.resolve("mytenant.mayocatshop.com"));
        Assert.assertEquals("mytenant", this.tenantResolver.resolve("mytenant.mayocatshop.com").getSlug());
    }

    @Test
    public void testMultitenancyTenantResolver2() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        Assert.assertNotNull(this.tenantResolver.resolve("mytenant.localhost"));
        Assert.assertEquals("mytenant", this.tenantResolver.resolve("mytenant.localhost").getSlug());
    }

    @Test
    public void testMultitenancyTenantResolver3() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        Assert.assertNull(this.tenantResolver.resolve("localhost"));
    }

    @Test
    public void testMultitenancyTenantResolver4() throws Exception
    {
        this.setUpExpectationsForMultitenancyActivated();

        Assert.assertNull(this.tenantResolver.resolve("mayocatshop.com"));
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

                allowing(configuration).getRootDomain();
                will(returnValue(null));
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

                allowing(configuration).getDefaultTenant();
                will(returnValue("mytenant"));
            }
        });
    }
}
