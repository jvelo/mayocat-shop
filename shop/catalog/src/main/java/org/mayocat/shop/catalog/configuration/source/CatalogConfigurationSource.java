package org.mayocat.shop.catalog.configuration.source;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationSource;
import org.mayocat.shop.catalog.configuration.shop.CatalogConfiguration;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("catalog")
public class CatalogConfigurationSource implements ConfigurationSource
{
    @Inject
    private CatalogConfiguration shopConfiguration;

    @Override
    public Object get()
    {
        return shopConfiguration;
    }
}
