/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import com.yammer.metrics.annotation.Timed
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
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/user")
@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@CompileStatic
class UserApi implements Resource {

    @Inject
    AccountsService accountsService

    @Inject
    WebContext context

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Authorized(roles = [ Role.ADMIN ])
    @ExistingTenant
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
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors())
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                    .entity("A user with this usernane or email already exists").type(MediaType.TEXT_PLAIN_TYPE)
                    .build())
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    @ExistingTenant
    UserApiObject getUser(@PathParam("slug") String slug)
    {
        def user = accountsService.findUserByEmailOrUserName(slug)
        if (user == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build())
        }

        UserApiObject userApiObject = new UserApiObject([
                _href: "/api/user/${user.slug}"
        ])
        userApiObject.withUser(user)
        userApiObject
    }
}
