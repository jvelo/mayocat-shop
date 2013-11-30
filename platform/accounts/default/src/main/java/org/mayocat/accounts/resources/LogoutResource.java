/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.Resource;
import org.xwiki.component.annotation.Component;

@Component(LogoutResource.PATH)
@Path(LogoutResource.PATH)
public class LogoutResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "logout";

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logout()
    {
        NewCookie newUserCookie = new NewCookie("username", "", "/", null, null, 0, false);
        NewCookie newPassCookie = new NewCookie("password", "", "/", null, null, 0, false);

        return Response.ok().cookie(newUserCookie, newPassCookie).build();
    }
}
