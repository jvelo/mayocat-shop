package org.mayocat.shop.shipping.configuration;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("shipping")
public class ShippingGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private ShippingSettings shippingSettings;

    @Override
    public Object get()
    {
        return shippingSettings;
    }
}
