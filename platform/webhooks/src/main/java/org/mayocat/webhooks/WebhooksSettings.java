package org.mayocat.webhooks;

import java.util.Collections;
import java.util.List;
import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("webhooks")
public class WebhooksSettings implements ExposedSettings
{
    @Override
    public String getKey() {
        return "webhooks";
    }

    private Configurable<List<Hook>> hooks = new Configurable(Collections.emptyList());

    public Configurable<List<Hook>> getHooks() {
        return hooks;
    }
}
