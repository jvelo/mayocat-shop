package org.mayocat.configuration.theme;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.theme.ThemeDefinition;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

/**
 * @version $Id$
 */
@Component("theme")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class ThemeGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private ThemeSettings themeSettings;

    private List<ThemeDefinition> themes;

    @Override
    public Object get()
    {
        return themeSettings;
    }
}
