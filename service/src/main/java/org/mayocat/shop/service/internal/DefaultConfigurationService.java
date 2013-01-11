package org.mayocat.shop.service.internal;

import javax.inject.Inject;

import org.mayocat.shop.configuration.tenant.ShopConfiguration;
import org.mayocat.shop.service.ConfigurationService;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultConfigurationService implements ConfigurationService
{
    @Inject
    private ShopConfiguration configuration;

    @Override
    public ShopConfiguration getConfiguration()
    {
        return this.configuration;
    }
}
