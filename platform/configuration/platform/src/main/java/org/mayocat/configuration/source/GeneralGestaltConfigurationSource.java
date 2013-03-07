package org.mayocat.configuration.source;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.general.GeneralSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("general")
public class GeneralGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private GeneralSettings generalSettings;

    @Override
    public Object get()
    {
        return generalSettings;
    }
}
