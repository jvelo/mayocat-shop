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
import org.mayocat.accounts.AccountsSettings
import org.mayocat.accounts.model.User
import org.mayocat.accounts.session.JerseyCookieSessionManager
import org.mayocat.configuration.ConfigurationService
import org.mayocat.rest.Resource
import org.mayocat.rest.error.ErrorUtil
import org.mayocat.rest.error.StandardError
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
    JerseyCookieSessionManager sessionManager

    @Inject
    ConfigurationService configurationService

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response login(@FormParam("username") String username, @FormParam("password") String password,
            @FormParam("remember") @DefaultValue("false") Boolean remember)
    {
        try {
            User user = accountsService.findUserByEmailOrUserName(username)

            if (user == null) {
                return ErrorUtil.buildError(Response.Status.UNAUTHORIZED, StandardError.INVALID_CREDENTIALS,
                        "Credentials are not correct");
            }

            if (!passwordManager.verifyPassword(password, user.getPassword())) {
                return ErrorUtil.buildError(Response.Status.UNAUTHORIZED, StandardError.INVALID_CREDENTIALS,
                        "Credentials are not correct");
            }

            if (!user.active && getSettings().userValidationRequiredForLogin.value) {
                return ErrorUtil.buildError(Response.Status.UNAUTHORIZED, StandardError.ACCOUNT_REQUIRES_VALIDATION,
                        "Account requires validation");
            }

            return Response.ok().cookie(sessionManager.getCookies(username, password, remember)).build()
        } catch (EncryptionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to log in.").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }

    private AccountsSettings getSettings()
    {
        return configurationService.getSettings(AccountsSettings.class)
    }
}
