/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.web

import com.google.common.base.Strings
import com.google.common.collect.Maps
import groovy.transform.CompileStatic
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.AccountsSettings
import org.mayocat.accounts.NoSuchPasswordResetKeyException
import org.mayocat.accounts.PasswordDoesNotMeetRequirementsException
import org.mayocat.accounts.UserNotFoundException
import org.mayocat.accounts.model.User
import org.mayocat.accounts.session.JerseyCookieSessionManager
import org.mayocat.accounts.web.object.LoginWebObject
import org.mayocat.accounts.web.object.PasswordResetRequestWebObject
import org.mayocat.accounts.web.object.PasswordResetWebObject
import org.mayocat.configuration.ConfigurationService
import org.mayocat.rest.Resource
import org.mayocat.rest.error.Error
import org.mayocat.rest.error.ErrorUtil
import org.mayocat.rest.error.StandardError
import org.mayocat.security.EncryptionException
import org.mayocat.security.PasswordManager
import org.mayocat.shop.front.views.WebView
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/login")
@Path("/login")
@CompileStatic
class LoginWebView implements Resource
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
    @Consumes(MediaType.APPLICATION_JSON)
    Response login(LoginWebObject loginWebObject)
    {
        try {
            Map<String, Object> data = Maps.newHashMap()
            Map<String, Object> loginAttemptData = Maps.newHashMap()

            data.put("loginAttempt", loginAttemptData)

            Error error;

            if (Strings.isNullOrEmpty(loginWebObject.username)
                    || Strings.isNullOrEmpty(loginWebObject.password))
            {
                error = new Error(Response.Status.BAD_REQUEST, StandardError.INSUFFICIENT_DATA, "Invalid login request");
            } else {
                User user = accountsService.findUserByEmailOrUserName(loginWebObject.username)

                if (user == null) {
                    error = new Error(Response.Status.UNAUTHORIZED, StandardError.INVALID_CREDENTIALS,
                            "Credentials are not correct");
                } else if (!passwordManager.verifyPassword(loginWebObject.password, user.getPassword())) {
                    error = new Error(Response.Status.UNAUTHORIZED, StandardError.INVALID_CREDENTIALS,
                            "Credentials are not correct");
                } else if (!user.active && getSettings().userValidationRequiredForLogin.value) {
                    error = new Error(Response.Status.UNAUTHORIZED, StandardError.ACCOUNT_REQUIRES_VALIDATION,
                            "Account requires validation");
                }
            }

            if (error) {
                loginAttemptData.put("error", error)
                return Response.status(Response.Status.UNAUTHORIZED).entity(new WebView().data(data)).build()
            }

            loginAttemptData.put("successful", true)

            return Response.ok().entity(new WebView().data(data)).cookie(sessionManager.
                    getCookies(loginWebObject.username, loginWebObject.password, loginWebObject.remember)).build()
        } catch (EncryptionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to log in.").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }

    @POST
    @Path("password-reset-request")
    @Consumes(MediaType.APPLICATION_JSON)
    def requestPasswordReset(PasswordResetRequestWebObject passwordResetRequest)
    {
        Map<String, Object> data = Maps.newHashMap()
        Map<String, Object> passwordResetRequestData = Maps.newHashMap()

        if (!passwordResetRequest || Strings.isNullOrEmpty(passwordResetRequest.identifier)) {
            passwordResetRequestData.put("error", new Error(Response.Status.BAD_REQUEST,
                    StandardError.INSUFFICIENT_DATA, "The password reset request is incomplete"));
        } else {
            try {
                accountsService.createPasswordResetRequest(passwordResetRequest.identifier)
            } catch (UserNotFoundException e) {
                passwordResetRequestData.put("error", new Error(Response.Status.NOT_FOUND,
                        StandardError.USER_NOT_FOUND, "No user matches this identifier"));
            }
        }

        passwordResetRequestData.put("successful", !passwordResetRequestData.containsKey("error"));

        // Push back in the context the email to which the reset link has been sent to so it can be displayed if needed
        passwordResetRequestData.put("email", passwordResetRequest.identifier);

        data.put("passwordResetRequest", passwordResetRequestData);

        if (passwordResetRequestData.containsKey("error")) {
            return Response.status(((Error) passwordResetRequestData.get("error")).getStatus()).
                    entity(new WebView().data(data)).build()
        } else {
            return new WebView().data(data)
        }
    }

    @POST
    @Path("password-reset")
    @Consumes(MediaType.APPLICATION_JSON)
    def resetPassword(PasswordResetWebObject passwordReset)
    {
        Map<String, Object> data = Maps.newHashMap()
        Map<String, Object> passwordResetData = Maps.newHashMap()

        if (!passwordReset || Strings.isNullOrEmpty(passwordReset.resetKey) || Strings.isNullOrEmpty(passwordReset.password)) {
            passwordResetData.put("error", new Error(Response.Status.BAD_REQUEST,
                    StandardError.INSUFFICIENT_DATA, "The password reset request is incomplete"));
        } else {
            try {
                accountsService.resetPassword(passwordReset.resetKey, passwordReset.password);
            } catch (NoSuchPasswordResetKeyException e) {
                passwordResetData.put("error", new Error(Response.Status.NOT_FOUND,
                        StandardError.PASSWORD_RESET_KEY_NOT_FOUND, "No user matches this identifier"));
            }
            catch (PasswordDoesNotMeetRequirementsException e) {
                passwordResetData.put("error", new Error(Response.Status.NOT_FOUND,
                        StandardError.PASSWORD_DOES_NOT_MEET_REQUIREMENTS, "Password does not meet requirements"));
            }
        }

        passwordResetData.put("successful", !passwordResetData.containsKey("error"));

        // Push back in the context the reset key so it can be displayed if needed
        passwordResetData.put("resetKey", passwordReset.resetKey);

        data.put("passwordReset", passwordResetData);

        if (passwordResetData.containsKey("error")) {
            return Response.status(((Error) passwordResetData.get("error")).getStatus()).
                    entity(new WebView().data(data)).build()
        } else {
            return new WebView().data(data)
        }

    }

    private AccountsSettings getSettings()
    {
        return configurationService.getSettings(AccountsSettings.class)
    }
}
