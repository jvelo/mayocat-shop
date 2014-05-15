/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import groovy.transform.CompileStatic
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.model.User
import org.mayocat.rest.Resource
import org.mayocat.security.Cipher
import org.mayocat.security.EncryptionException
import org.mayocat.security.PasswordManager
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.DefaultValue
import javax.ws.rs.FormParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/login")
@Path("/api/login")
@CompileStatic
class LoginApi implements Resource 
{
    @Inject
    AccountsService accountsService

    @Inject
    PasswordManager passwordManager

    @Inject
    Cipher crypter

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response login(@FormParam("username") String username, @FormParam("password") String password,
                          @FormParam("remember") @DefaultValue("false") Boolean remember)
    {
        try {
            User user = accountsService.findUserByEmailOrUserName(username)

            if (user == null) {
                // Don't give more information than this
                return Response.noContent().status(Response.Status.UNAUTHORIZED).build()
            }

            if (!passwordManager.verifyPassword(password, user.getPassword())) {
                // Don't give more information than this
                return Response.noContent().status(Response.Status.UNAUTHORIZED).build()
            }

            // Find out some cookie parameters we will need
            int ageWhenRemember = 60 * 60 * 24 * 15 // TODO make configurable
            // String domain = uri.geBaseUri().getHost()
            // TODO set domain when at least two dots ? Or config ?
            // See http://curl.haxx.se/rfc/cookie_spec.html

            // Create the new cookies to be sent with the response
            NewCookie newUserCookie =
                    new NewCookie("username", crypter.encrypt(username), "/", null, null, remember ? ageWhenRemember : -1,
                            false)
            NewCookie newPassCookie =
                    new NewCookie("password", crypter.encrypt(password), "/", null, null, remember ? ageWhenRemember : -1,
                            false)

            return Response.ok().cookie(newUserCookie, newPassCookie).build()

        } catch (EncryptionException e) {
            // Don't give more information than this
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to log in.").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }
}
