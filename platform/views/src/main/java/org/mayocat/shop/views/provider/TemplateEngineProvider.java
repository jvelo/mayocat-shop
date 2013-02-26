package org.mayocat.shop.views.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.views.TemplateEngine;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

/**
 * @version $Id$
 */
public class TemplateEngineProvider implements Provider<TemplateEngine>
{
    @Inject
    private ComponentManager componentManager;

    @Override
    public TemplateEngine get()
    {
        try {
            return this.componentManager.getInstance(TemplateEngine.class, "handlebars");
        } catch (ComponentLookupException e) {
            throw new IllegalStateException("Could not find default template engine in component manager", e);
        }
    }
}
