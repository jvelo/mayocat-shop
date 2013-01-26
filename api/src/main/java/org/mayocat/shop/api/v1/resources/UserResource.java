package org.mayocat.shop.api.v1.resources;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.context.Context;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.service.AccountsService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("/api/1.0/user/")
@Path("/api/1.0/user/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@Authorized
public class UserResource implements Resource
{
    @Inject
    private AccountsService accountsService;

    @Inject
    private Execution execution;

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Authorized(roles = { Role.ADMIN })
    public Response addUser(@Valid User user)
    {
        Context context = execution.getContext();
        try {
            if (context.getUser() == null) {
                // This can only mean there is no user recorded in database,
                // and this is the request to create the initial user.

                this.accountsService.createInitialUser(user);
            } else {
                this.accountsService.createUser(user);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                    .entity("A user with this usernane or email already exists").type(MediaType.TEXT_PLAIN_TYPE)
                    .build());
        }
    }

    @GET
    @Path("_me")
    public User getCurrentUser()
    {
        Context context = execution.getContext();
        return context.getUser();
    }

    @Path("{slug}")
    @GET
    @Timed
    public User getUser(@PathParam("slug") String slug)
    {
        return accountsService.findUserByEmailOrUserName(slug);
    }
}
