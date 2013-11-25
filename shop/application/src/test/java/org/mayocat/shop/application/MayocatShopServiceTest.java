package org.mayocat.shop.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MayocatShopService}.
 *
 * @version $Id$
 */
public class MayocatShopServiceTest
{

    private MayocatShopService service;

    @Before
    public void setUpService() throws Exception
    {
        service = new MayocatShopService();
        service.run(new String[]{"server", "target/test-classes/mayocat.yml"});
    }

    @Test
    public void testRunMayocat()
    {
        Assert.assertNotNull(service);
    }

}
