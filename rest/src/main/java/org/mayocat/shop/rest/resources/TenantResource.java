package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.context.Context;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.service.TenantService;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component("TenantResource")
@Path("/tenant/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantResource implements Resource
{

    @Inject
    private Execution execution;

    @Inject
    private TenantService tenantService;

    @GET
    @Authorized
    public UserAndTenant currentTenant()
    {
        UserAndTenant userAndTenant = new UserAndTenant();
        userAndTenant.setTenant(execution.getContext().getTenant());
        userAndTenant.setUser(execution.getContext().getUser());
        return userAndTenant;
    }

    @PUT
    @Authorized
    public Response updateTenant(Tenant updatedTenant)
    {
        try {
            Context context = execution.getContext();
            if (context.getTenant() == null) {
                // Should not happen
                return Response.status(404).build();
            } else {
                updatedTenant.setSlug(context.getTenant().getSlug());
                this.tenantService.update(updatedTenant);
            }

            return Response.ok().build();

        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    public Response createTenant(UserAndTenant userAndTenant)
    {
        // TODO
        return Response.ok().build();
    }

    private class UserAndTenant
    {
        private User user;

        private Tenant tenant;

        public Tenant getTenant()
        {
            return tenant;
        }

        public void setTenant(Tenant tenant)
        {
            this.tenant = tenant;
        }

        public User getUser()
        {
            return user;
        }

        public void setUser(User user)
        {
            this.user = user;
        }
    }

}
