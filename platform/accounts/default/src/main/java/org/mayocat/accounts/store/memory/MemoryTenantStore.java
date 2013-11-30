/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store.memory;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.context.WebContext;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link TenantStore}.
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryTenantStore extends BaseEntityMemoryStore<Tenant> implements TenantStore
{
    private Predicate<? super Tenant> withDefaultHost(final String host)
    {
        return new Predicate<Tenant>()
        {
            public boolean apply(@Nullable Tenant input)
            {
                return input.getDefaultHost().equals(host);
            }
        };
    }

    public Tenant findBySlug(String slug)
    {
        return FluentIterable.from(findAll(0, 0)).filter(withSlug(slug)).first().orNull();
    }

    public Tenant findByDefaultHost(String host)
    {
        return FluentIterable.from(findAll(0, 0)).filter(withDefaultHost(host)).first().orNull();
    }

    public void updateConfiguration(TenantConfiguration configuration)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
