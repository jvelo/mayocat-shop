/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import java.net.URL;

import javax.inject.Inject;

import org.jvnet.inflector.Noun;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.WebContext;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.PluralForm;

/**
 * @version $Id$
 */
public abstract class AbstractGenericEntityURLFactory<E extends Entity> implements EntityURLFactory<E>
{
    @Inject
    private WebContext context;

    @Inject
    private URLHelper urlHelper;

    @Override
    public URL create(E entity, Tenant tenant)
    {
        return this.create(entity, tenant, URLType.PUBLIC);
    }

    @Override
    public URL create(E entity)
    {
        return this.create(entity, context.getTenant(), URLType.PUBLIC);
    }

    @Override
    public URL create(E entity, Tenant tenant, URLType type)
    {
        String path = "/" + getPluralForm(entity) + "/" + entity.getSlug();

        switch (type) {
            case API:
                path = "/api/" + path;
                return urlHelper.getTenantPlatformURL(tenant, path);
            case PUBLIC:
            default:
                return urlHelper.getTenantWebURL(tenant, path);
        }
    }

    protected String getPluralForm(E entity)
    {
        if (entity.getClass().isAnnotationPresent(PluralForm.class)) {
            return entity.getClass().getAnnotation(PluralForm.class).value();
        }

        return Noun.pluralOf(getEntityName(entity));
    }

    protected abstract String getEntityName(E entity);
}
