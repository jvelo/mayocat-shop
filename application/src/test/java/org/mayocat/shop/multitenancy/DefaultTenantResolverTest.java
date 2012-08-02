package org.mayocat.shop.multitenancy;

import javax.inject.Provider;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.configuration.MultitenancyConfiguration;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.TenantStore;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.test.AbstractMockingComponentTestCase;
import org.xwiki.test.annotation.MockingRequirement;

public class DefaultTenantResolverTest extends AbstractMockingComponentTestCase
{

    @MockingRequirement(exceptions = MultitenancyConfiguration.class)
    private DefaultTenantResolver tenantResolver;

    private MultitenancyConfiguration configuration;

    private Provider<TenantStore> provider;

    private TenantStore providedTenantStore;

    /**
     * Setup mock dependencies before initializing the @MockingRequirement components.
     */
    @Override
    protected void setupDependencies() throws Exception
    {
        getMockery().setImposteriser(ClassImposteriser.INSTANCE);
        configuration = getMockery().mock(MultitenancyConfiguration.class);
        providedTenantStore = getMockery().mock(TenantStore.class, "actual implementation");

        DefaultComponentDescriptor<MultitenancyConfiguration> cd =
            new DefaultComponentDescriptor<MultitenancyConfiguration>();
        cd.setRoleType(MultitenancyConfiguration.class);
        this.getComponentManager().registerComponent(cd, this.configuration);
        
        DefaultComponentDescriptor<TenantStore> cd2 =
            new DefaultComponentDescriptor<TenantStore>();
        cd2.setRoleType(TenantStore.class);
        this.getComponentManager().registerComponent(cd2, this.providedTenantStore);
    }

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        configuration = this.getComponentManager().getInstance(MultitenancyConfiguration.class);
        providedTenantStore = this.getComponentManager().getInstance(TenantStore.class);

        
        DefaultParameterizedType providerType = new DefaultParameterizedType(Provider.class.getComponentType(), Provider.class, TenantStore.class);
        this.provider = this.getComponentManager().getInstance(providerType);

        getMockery().checking(new Expectations()
        {
            {
                allowing(configuration).isActivated();
                will(returnValue(false));

                allowing(configuration).getDefaultTenant();
                will(returnValue("mytenant"));
                
                allowing(configuration).getRootDomain();
                will(returnValue(null));

                allowing(provider).get();
                will(returnValue(providedTenantStore));

                //allowing(providedTenantStore).findByHandle(with(any(String.class)));
                //will(returnValue(null));

                allowing(providedTenantStore).findByHandle("mytenant");
                will(returnValue(new Tenant("mytenant")));
                
                allowing(providedTenantStore).create(with(any(Tenant.class)));
                
            }
        });
    }

    @Test
    public void testMultitenancyNotActivatedReturnsDefaultTenant() throws Exception
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

        Assert.assertEquals("mytenant", this.tenantResolver.resolve("google.com").getHandle());
        Assert.assertEquals("mytenant", this.tenantResolver.resolve("localhost").getHandle());
        Assert.assertEquals("mytenant", this.tenantResolver.resolve("Anything").getHandle());
    }
}
