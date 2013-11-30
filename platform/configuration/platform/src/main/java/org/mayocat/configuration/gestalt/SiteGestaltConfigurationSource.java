/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.gestalt;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.context.WebContext;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.yammer.dropwizard.json.ObjectMapperFactory;

/**
 * @version $Id$
 */
@Component("site")
public class SiteGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private WebContext context;

    @Inject
    private ObjectMapperFactory objectMapperFactory;

    @Override
    public Object get()
    {
        Tenant tenant = context.getTenant();
        Map<String, Object> result = Maps.newHashMap();
        String domain = null;
        if (multitenancySettings.isActivated()) {
            if (tenant != null) {
                domain = !Strings.isNullOrEmpty(tenant.getDefaultHost()) ? tenant.getDefaultHost() :
                        tenant.getSlug() + "." + siteSettings.getDomainName();
            }
        }
        if (domain == null) {
            domain = siteSettings.getDomainName();
        }

        result.put("domain", domain);
        result.put("url", "http://" + domain);

        return result;
    }
}
