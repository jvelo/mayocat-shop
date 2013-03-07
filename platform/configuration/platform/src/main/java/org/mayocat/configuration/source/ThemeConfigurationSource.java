package org.mayocat.configuration.source;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationSource;
import org.mayocat.configuration.theme.ThemeConfiguration;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

/**
 * @version $Id$
 */
@Component("theme")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class ThemeConfigurationSource implements ConfigurationSource
{
    @Inject
    private ThemeConfiguration themeConfiguration;

    private List<Theme> themes;

    @Override
    public Object get()
    {
        return themeConfiguration;
    }
}
