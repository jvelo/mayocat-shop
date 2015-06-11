package org.mayocat.webhooks;

import javax.inject.Inject;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("webhooks")
public class WebhooksGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private WebhooksSettings settings;

    @Override
    public Object get() {
        return settings;
    }
}
