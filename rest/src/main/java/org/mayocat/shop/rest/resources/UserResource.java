package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.Capability;
import org.mayocat.shop.authorization.Context;
import org.mayocat.shop.authorization.PasswordManager;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.authorization.capability.shop.AddProduct;
import org.mayocat.shop.authorization.capability.shop.AddUser;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.model.UserRole;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.RoleStore;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserRoleStore;
import org.mayocat.shop.store.UserStore;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("UserResource")
@Path("/user/")
public class UserResource implements Resource
{

    @Inject
    private Provider<UserStore> store;

    @Inject
    private Provider<RoleStore> roleStore;
    
    @Inject
    private Provider<UserRoleStore> userRoleStore;
    
    @Inject
    private PasswordManager passwordManager;

    @PUT
    @Timed
    public Response addUser(@Authorized(AddUser.class) Context context, @Valid User user)
    {
        try {
            // Hash the provided password
            user.setPassword(this.passwordManager.hashPassword(user.getPassword()));

            // Persist
            this.store.get().create(user);

            if (context.getUser() == null) {
                // This means the shop has no user yet.
                Role role = new Role();
                role.setName(Role.RoleName.ADMIN);
                role.addToCapabilities(new AddUser());
                role.addToCapabilities(new AddProduct());
                roleStore.get().create(role);
                
                UserRole userRole = new UserRole();
                userRole.setRole(role);
                userRole.setUser(user);
                userRoleStore.get().create(userRole);
            }
            
            return Response.ok().build();
            
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                .entity("A user with this usernane or email already exists").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }
}
