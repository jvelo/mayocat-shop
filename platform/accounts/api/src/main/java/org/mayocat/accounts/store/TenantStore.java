/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store;

import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, UUID>
{
    Tenant findBySlug(String slug);

    Tenant findByDefaultHost(String host);

    void updateConfiguration(TenantConfiguration configuration);
}
