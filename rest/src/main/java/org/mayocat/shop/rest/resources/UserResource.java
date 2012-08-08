package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.Context;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.authorization.capability.shop.AddUser;
import org.mayocat.shop.model.User;
import org.mayocat.shop.service.UserService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("UserResource")
@Path("/user/")
public class UserResource implements Resource
{

    @Inject
    private UserService userService;

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(@Authorized(AddUser.class) Context context, @Valid User user)
    {
        try {
            if (context.getUser() == null) {
                // This can only mean there is no user recorded in database,
                // and this is the request to create the initial user.

                this.userService.createInitialUser(user);
            }

            else {
                this.userService.create(user);
            }

            return Response.ok().build();

        } catch (StoreException e) {
            throw new WebApplicationException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                .entity("A user with this usernane or email already exists").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    @GET
    @Path("_me")
    @Produces({"application/json; charset=UTF-8"})
    public User getCurrentUser(@Authorized Context context)
    {
        return context.getUser();
    }

    @Path("{handle}")
    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public User getUser(@Authorized Context context, @PathParam("handle") String handle)
    {
        try {
            return userService.findByEmailOrUserName(handle);
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }

    }
}
