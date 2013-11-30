/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeZone;
import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.representations.TenantRepresentation;
import org.mayocat.accounts.representations.UserAndTenantRepresentation;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.rest.Resource;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(MeResource.PATH)
@Path(MeResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
public class MeResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "me";

    @Inject
    private AccountsService accountsService;

    @Inject
    private WebContext context;

    @Inject
    private GeneralSettings generalSettings;

    @GET
    public UserAndTenantRepresentation getCurrentUser()
    {
        UserAndTenantRepresentation userAndTenant = new UserAndTenantRepresentation();

        if (this.context.getTenant() != null) {
            userAndTenant
                    .setTenant(new TenantRepresentation(getGlobalTimeZone(), this.context.getTenant()));
        }
        userAndTenant.setUser(context.getUser());

        return userAndTenant;
    }

    private DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue());
    }
}
