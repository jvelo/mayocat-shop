package org.mayocat.shop.configuration.source;

import javax.inject.Inject;

import org.mayocat.shop.configuration.ConfigurationSource;
import org.mayocat.shop.configuration.theme.ThemeConfiguration;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("theme")
public class ThemeConfigurationSource implements ConfigurationSource
{
    @Inject
    private ThemeConfiguration themeConfiguration;

    @Override
    public Object get()
    {
        return themeConfiguration;
    }
}
