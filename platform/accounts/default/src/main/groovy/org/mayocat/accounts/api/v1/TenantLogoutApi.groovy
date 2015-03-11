/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import groovy.transform.CompileStatic
import org.mayocat.rest.annotation.ExistingTenant
import org.xwiki.component.annotation.Component

import javax.ws.rs.Path

/**
 * Same as {@link LogoutApi} but for a tenant.
 *
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/logout")
@Path("/tenant/{tenant}/api/logout")
@ExistingTenant
@CompileStatic
class TenantLogoutApi extends LogoutApi
{
}
