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
import org.mayocat.accounts.api.v1.object.UserApiObject
import org.mayocat.accounts.model.Role
import org.mayocat.accounts.model.User
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.context.WebContext
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.InvalidEntityException
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/user")
@Path("/tenant/{tenant}/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@CompileStatic
@ExistingTenant
class TenantUserApi implements Resource {

    @Inject
    AccountsService accountsService

    @Inject
    WebContext context

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Authorized(roles = [ Role.ADMIN ])
    Response addUser(@Valid UserApiObject userApiObject)
    {
        User user = userApiObject.toUser()
        try {
            if (context.user == null) {
                // This can only mean there is no user recorded in database,
                // and this is the request to create the initial user.
                this.accountsService.createInitialUser(user)
            } else {
                this.accountsService.createUser(user)
            }

            return Response.ok().build()
        } catch (InvalidEntityException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid user").type(MediaType.TEXT_PLAIN_TYPE)
                    .build())
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                    .entity("A user with this username or email already exists").type(MediaType.TEXT_PLAIN_TYPE)
                    .build())
        }
    }

    @Path("{slug}")
    @GET
    UserApiObject getUser(@PathParam("slug") String slug)
    {
        def user = accountsService.findUserByEmailOrUserName(slug)
        if (user == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build())
        }

        UserApiObject userApiObject = new UserApiObject([
                _href: "${context.request.tenantPrefix}/api/user/${user.slug}"
        ])
        userApiObject.withUser(user)
        userApiObject
    }
}
