package org.mayocat.shop.service.internal;

import javax.inject.Inject;

import org.mayocat.shop.configuration.general.GeneralConfiguration;
import org.mayocat.shop.configuration.shop.ShopConfiguration;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.service.ConfigurationService;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Multimap;

/**
 * @version $Id$
 */
@Component
public class DefaultConfigurationService implements ConfigurationService
{
    @Inject
    private ShopConfiguration configuration;

    @Inject
    private Execution execution;

    @Override
    public ShopConfiguration getConfiguration()
    {
        Multimap<String, Object> tenantConfiguration = execution.getContext().getTenant().getConfiguration();
        ShopConfiguration gestaltConfiguration = new ShopConfiguration();

        return this.configuration;
    }
}
