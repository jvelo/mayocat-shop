/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.context.WebContext;
import org.mayocat.entity.DataLoaderAssistant;
import org.mayocat.entity.EntityData;
import org.mayocat.entity.LoadingOption;
import org.mayocat.model.Entity;
import org.mayocat.model.Owned;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("tenant")
public class TenantDataLoader implements DataLoaderAssistant
{
    @Inject
    private Provider<TenantStore> tenantStore;

    @Inject
    private WebContext webContext;

    public <E extends Entity> void load(EntityData<E> entity, LoadingOption... options)
    {
        if (webContext.getTenant() != null) {
            entity.setData(Tenant.class, webContext.getTenant());
        }
        else {
            if (Owned.class.isAssignableFrom(entity.getEntity().getClass())) {
                Owned owned = (Owned) entity.getEntity();
                entity.setData(Tenant.class, this.tenantStore.get().findById(owned.getTenantId()));
            }
        }
    }

    public <E extends Entity> void loadList(List<EntityData<E>> entities, LoadingOption... options)
    {
        // TODO improve perf by doing only one query
        for (EntityData<E> entityData : entities) {
            load(entityData, options);
        }
    }

    public Integer priority()
    {
        return 500;
    }
}
