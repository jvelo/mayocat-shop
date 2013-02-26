package org.mayocat.shop.configuration.source;

import javax.inject.Inject;

import org.mayocat.shop.configuration.ConfigurationSource;
import org.mayocat.shop.configuration.general.GeneralConfiguration;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("general")
public class GeneralConfigurationSource implements ConfigurationSource
{
    @Inject
    private GeneralConfiguration generalConfiguration;

    @Override
    public Object get()
    {
        return generalConfiguration;
    }
}
