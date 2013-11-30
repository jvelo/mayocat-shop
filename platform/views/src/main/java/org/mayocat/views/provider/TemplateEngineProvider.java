/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.views.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.views.TemplateEngine;
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
