package org.mayocat.shop.catalog.configuration.source;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("catalog")
public class CatalogGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private CatalogSettings catalogSettings;

    @Override
    public Object get()
    {
        return catalogSettings;
    }
}
