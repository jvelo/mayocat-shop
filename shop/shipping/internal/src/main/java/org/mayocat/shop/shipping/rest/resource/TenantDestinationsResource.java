/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mayocat.rest.annotation.ExistingTenant;
import org.xwiki.component.annotation.Component;

/**
 * Same as {@link org.mayocat.shop.shipping.rest.resource.DestinationsResource} but for a tenant.
 *
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/shipping/destinations/")
@Path("/tenant/{tenant}/api/shipping/destinations/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class TenantDestinationsResource extends DestinationsResource
{
}
