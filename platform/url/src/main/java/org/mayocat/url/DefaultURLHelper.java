/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.context.WebContext;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

/**
 * @version $Id$
 */
@Component
public class DefaultURLHelper implements URLHelper
{
    @Inject
    private SiteSettings siteSettings;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private WebContext context;

    public String getContextWebBaseURL()
    {
        return getTenantWebBaseURL(context.getTenant());
    }

    public String getContextPlatformBaseURL()
    {
        return getTenantPlatformBaseURL(context.getTenant());
    }

    public String getTenantWebBaseURL(Tenant tenant)
    {
        return getTenantWebURL(tenant, "").toString();
    }

    public String getTenantPlatformBaseURL(Tenant tenant)
    {
        return getTenantPlatformURL(tenant, "").toString();
    }

    public String getContextWebDomain()
    {
        return getTenantWebDomain(context.getTenant());
    }

    public String getContextPlatformDomain()
    {
        return getTenantPlatformDomain(context.getTenant());
    }

    public String getTenantWebDomain(Tenant tenant)
    {
        if (siteSettings.getWebDomainName().isPresent()) {
            return getTenantDomainName(siteSettings.getWebDomainName().get(), tenant);
        }
        return getTenantPlatformDomain(tenant);
    }

    public String getTenantPlatformDomain(Tenant tenant)
    {
        return getTenantDomainName(siteSettings.getDomainName(), tenant);
    }

    public URL getContextWebURL(String path)
    {
        return getTenantWebURL(context.getTenant(), path);
    }

    public URL getContextBackOfficeURL(String path)
    {
        return getTenantBackOfficeURL(context.getTenant(), path);
    }

    public URL getContextPlatformURL(String path)
    {
        return getTenantPlatformURL(context.getTenant(), path);
    }

    public URL getTenantWebURL(Tenant tenant, String path)
    {
        return getURL(getTenantWebDomain(tenant), path);
    }

    public URL getTenantBackOfficeURL(Tenant tenant, String path)
    {
        String backOfficeDomain;
        String realPath;
        if (siteSettings.getBackOfficeDomainName().isPresent()) {
            backOfficeDomain = siteSettings.getBackOfficeDomainName().get();
            realPath = path;
        } else {
            backOfficeDomain = getTenantPlatformDomain(tenant);
            realPath = "/admin" + (path.startsWith("/") ? "" : "/") + path;
        }
        return getURL(backOfficeDomain, realPath);
    }

    public URL getTenantPlatformURL(Tenant tenant, String path)
    {
        return getURL(getTenantPlatformDomain(tenant), path);
    }

    // Private helpers
    // -----------------------------------------------------------------------------------------------------------------

    private String getTenantDomainName(String domainName, Tenant tenant)
    {
        if (!multitenancySettings.isActivated() || tenant == null) {
            return domainName;
        } else {

        }
        return Strings.isNullOrEmpty(tenant.getDefaultHost()) ?
                tenant.getSlug() + "." + domainName :
                tenant.getDefaultHost();
    }

    private URL getURL(String domain, String path)
    {
        URL url = null;
        String urlAsString = "http://" + domain;
        if (!Strings.isNullOrEmpty(path)) {
            if (!path.startsWith("/")) {
                urlAsString += "/" + path;
            } else {
                urlAsString += path;
            }
        }
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            new RuntimeException(e);
        }
        return url;
    }
}
