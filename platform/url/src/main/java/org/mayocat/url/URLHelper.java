/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import java.net.URL;

import org.mayocat.accounts.model.Tenant;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface URLHelper
{
    String getContextWebBaseURL();

    String getContextPlatformBaseURL();

    String getTenantWebBaseURL(Tenant tenant);

    String getTenantPlatformBaseURL(Tenant tenant);

    String getContextWebDomain();

    String getContextPlatformDomain();

    String getTenantWebDomain(Tenant tenant);

    String getTenantPlatformDomain(Tenant tenant);

    URL getContextWebURL(String path);

    URL getContextBackOfficeURL(String path);

    URL getContextPlatformURL(String path);

    URL getTenantWebURL(Tenant tenant, String path);

    URL getTenantBackOfficeURL(Tenant tenant, String path);

    URL getTenantPlatformURL(Tenant tenant, String path);
}
