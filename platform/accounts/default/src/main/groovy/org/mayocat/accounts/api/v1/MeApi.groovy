/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.PasswordDoesNotMeetRequirementsException
import org.mayocat.accounts.WrongPasswordException
import org.mayocat.accounts.api.v1.object.PasswordChangeWebObject
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.api.v1.object.UserAndTenantApiObject
import org.mayocat.accounts.api.v1.object.UserApiObject
import org.mayocat.accounts.session.JerseyCookieSessionManager
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.rest.Resource
import org.mayocat.rest.error.ErrorUtil
import org.mayocat.rest.error.StandardError
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/me")
@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@CompileStatic
class MeApi implements Resource
{
    @Inject
    AccountsService accountsService

    @Inject
    WebContext context

    @Inject
    GeneralSettings generalSettings

    @Inject
    JerseyCookieSessionManager sessionManager

    @GET
    def getCurrentUser()
    {
        UserAndTenantApiObject userAndTenantApiObject = new UserAndTenantApiObject()

        if (this.context.tenant != null) {
            TenantApiObject tenantApiObject = new TenantApiObject([
                    _href: "/api/tenant/"
            ])
            tenantApiObject.withTenant(this.context.tenant, globalTimeZone)
            userAndTenantApiObject.tenant = tenantApiObject
        }

        UserApiObject userApiObject = new UserApiObject([
                _href: "/api/me/"
        ])
        userApiObject.withUser(context.user)

        userAndTenantApiObject.user = userApiObject

        userAndTenantApiObject
    }

    @POST
    @Path("password")
    @Authorized
    def updatePassword(PasswordChangeWebObject passwordChange)
    {
        if (!context.user) {
            return ErrorUtil.buildError(Response.Status.UNAUTHORIZED, StandardError.REQUIRES_VALID_USER,
                    "Can't change the password for nobody");
        } else {
            try {
                accountsService.changePassword(context.user, passwordChange.currentPassword, passwordChange.newPassword)
            } catch (WrongPasswordException e) {
                return ErrorUtil.buildError(Response.Status.UNAUTHORIZED, StandardError.INVALID_CREDENTIALS,
                        "Credentials are not correct");
            } catch (PasswordDoesNotMeetRequirementsException e) {
                return ErrorUtil.buildError(Response.Status.BAD_REQUEST,
                        StandardError.PASSWORD_DOES_NOT_MEET_REQUIREMENTS, e.getMessage());
            }
        }

        return Response.noContent()
                .cookie(sessionManager.getCookies(context.user.slug, passwordChange.newPassword, false)).build()
    }

    DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue())
    }
}
