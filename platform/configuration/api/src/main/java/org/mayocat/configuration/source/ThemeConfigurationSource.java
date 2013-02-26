package org.mayocat.configuration.source;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationSource;
import org.mayocat.configuration.theme.ThemeConfiguration;
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
