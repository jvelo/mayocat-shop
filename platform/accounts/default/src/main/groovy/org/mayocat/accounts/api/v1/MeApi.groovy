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
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.api.v1.object.UserAndTenantApiObject
import org.mayocat.accounts.api.v1.object.UserApiObject
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.rest.Resource
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/api/me")
@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@CompileStatic
class MeApi implements Resource {
    
    @Inject
    AccountsService accountsService

    @Inject
    WebContext context

    @Inject
    GeneralSettings generalSettings

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

    DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue())
    }
}
