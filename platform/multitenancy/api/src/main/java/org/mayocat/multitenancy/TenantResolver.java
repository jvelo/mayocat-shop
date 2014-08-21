/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.multitenancy;

import org.mayocat.accounts.model.Tenant;
import org.xwiki.component.annotation.Role;

/**
 * Resolve which tenant correspond to a certain hostname and/or request path.
 */
@Role
public interface TenantResolver
{
    /**
     * @param host the host to resolve the tenant for
     * @return the resolved tenant or null if no tenant is found for the passed host
     */
    Tenant resolve(String host);

    /**
     * @param host the host to resolve the tenant for
     * @param path the path to resolve the tenant for
     * @return the resolved tenant or null if no tenant is found for the passed host and path combination
     */
    Tenant resolve(String host, String path);
}
