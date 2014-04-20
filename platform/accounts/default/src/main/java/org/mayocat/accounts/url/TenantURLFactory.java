/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.url;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.WebContext;
import org.mayocat.url.AbstractEntityURLFactory;
import org.mayocat.url.EntityURLFactory;
import org.mayocat.url.URLType;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class TenantURLFactory extends AbstractEntityURLFactory<Tenant> implements EntityURLFactory<Tenant>
{
    @Inject
    private WebContext context;

    public URL create(Tenant entity, Tenant tenant)
    {
        return this.create(entity, tenant, URLType.PUBLIC);
    }

    @Override
    public URL create(Tenant entity)
    {
        return this.create(entity, this.context.getTenant(), URLType.PUBLIC);
    }

    @Override
    public URL create(Tenant entity, Tenant tenant, URLType type)
    {
        try {
            switch (type) {
                case API:
                    return new URL(getSchemeAndDomain(tenant) + "/api/tenant/");
                case PUBLIC:
                default:
                    return new URL(getSchemeAndDomain(tenant));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
