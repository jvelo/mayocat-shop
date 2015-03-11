/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.multitenancy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mayocat.event.EventListener;
import org.xwiki.component.annotation.Component;

@Component
@Named("defaultResolverEventListener")
public class DefaultTenantResolverServletEventListener
        implements ServletRequestListener, EventListener
{
    @Inject
    private TenantResolver defaultTenantResolver;

    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        try {
            DefaultTenantResolver dtr =
                    (DefaultTenantResolver) this.defaultTenantResolver;
            if (dtr != null) {
                dtr.requestDestroyed();
            }
        } catch (ClassCastException e) {
            // This mean we are not using the default slug tenant resolver...
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
    }
}
