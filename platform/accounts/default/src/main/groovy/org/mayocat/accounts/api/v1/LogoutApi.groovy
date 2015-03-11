/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import groovy.transform.CompileStatic
import org.mayocat.rest.Resource
import org.xwiki.component.annotation.Component

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/logout")
@Path("/api/logout")
@CompileStatic
class LogoutApi implements Resource
{
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logout()
    {
        NewCookie newUserCookie = new NewCookie("username", "", "/", null, null, 0, false)
        NewCookie newPassCookie = new NewCookie("password", "", "/", null, null, 0, false)

        Response.ok().cookie(newUserCookie, newPassCookie).build()
    }
}
