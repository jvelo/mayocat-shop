/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.api.v1

import groovy.transform.CompileStatic
import org.mayocat.rest.resources.TenantImageApi
import org.xwiki.component.annotation.Component

import javax.ws.rs.Path

/**
 * @version $Id$
 */
@Component("/api/images/")
@Path("/api/images/")
@CompileStatic
class ImageApi extends TenantImageApi
{
}
