/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.multitenancy;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.MultitenancySettings;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Component
public class TenantResolverProvider implements Provider<TenantResolver>
{
    @Inject
    private MultitenancySettings configuration;

    @Inject
    private Logger logger;

    @Inject
    private ComponentManager componentManager;

    @Override
    public TenantResolver get()
    {
        try {
            return this.componentManager.getInstance(TenantResolver.class, this.configuration.getResolver());
        } catch (ComponentLookupException e) {
            try {
                this.logger.error(
                    "Failed to lookup instance of TenantResolver with hint [{}], trying default implementation",
                    this.configuration.getResolver());
                return this.componentManager.getInstance(TenantResolver.class);
            } catch (ComponentLookupException e1) {
                throw new RuntimeException("Failed to lookup any instance of a tenant resolver", e1);
            }
        }
    }

}
