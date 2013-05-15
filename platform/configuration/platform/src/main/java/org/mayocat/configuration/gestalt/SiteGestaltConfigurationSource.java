package org.mayocat.configuration.gestalt;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.SiteSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("site")
public class SiteGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private SiteSettings siteSettings;

    @Override
    public Object get()
    {
        return siteSettings;
    }
}
