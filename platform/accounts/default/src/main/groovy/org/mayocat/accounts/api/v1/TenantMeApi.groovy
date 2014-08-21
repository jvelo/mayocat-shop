/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import groovy.transform.CompileStatic
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.rest.annotation.ExistingTenant
import org.xwiki.component.annotation.Component

import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Same as the global {@link MeApi}, but for tenants
 *
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/me")
@Path("/tenant/{tenant}/api/me")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@ExistingTenant
@CompileStatic
class TenantMeApi extends MeApi
{
}
