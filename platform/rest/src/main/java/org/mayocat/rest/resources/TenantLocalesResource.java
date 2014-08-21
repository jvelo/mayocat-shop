/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mayocat.rest.annotation.ExistingTenant;
import org.xwiki.component.annotation.Component;

/**
 * Same as {@link org.mayocat.rest.resources.LocalesResource} but for a tenant.
 *
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/locales")
@Path("/tenant/{tenant}/api/locales")
@Produces(MediaType.APPLICATION_JSON)
@ExistingTenant
public class TenantLocalesResource extends LocalesResource
{
}
