/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.model.Entity;

/**
 * @version $Id$
 */
public abstract class AbstractEntityURLFactory<E extends Entity> implements EntityURLFactory<E>
{
    @Inject
    private SiteSettings siteSettings;

    protected String getSchemeAndDomain(Tenant tenant)
    {
        return "http://" + getDomain(tenant);
    }

    protected String getDomain(Tenant tenant)
    {
        return StringUtils
                .defaultIfBlank(tenant.getDefaultHost(), tenant.getSlug() + "." + siteSettings.getDomainName());
    }
}
