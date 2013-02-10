package org.mayocat.shop.configuration.source;

import javax.inject.Inject;

import org.mayocat.shop.configuration.ConfigurationSource;
import org.mayocat.shop.configuration.shop.ShopConfiguration;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("shop")
public class ShopConfigurationSource implements ConfigurationSource
{
    @Inject
    private ShopConfiguration shopConfiguration;

    @Override
    public Object get()
    {
        return shopConfiguration;
    }
}
