package org.mayocat.accounts.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;
import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.representations.TenantRepresentation;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.authorization.Gatekeeper;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Context;
import org.mayocat.context.Execution;
import org.mayocat.model.Addon;
import org.mayocat.model.AddonFieldType;
import org.mayocat.model.AddonSource;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.support.AddonsRepresentationUnmarshaller;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;

/**
 * This resource allow access to the current tenant information. For complete tenant management API, see the
 * TenantManagerResource in the manager module.
 */
@Component(TenantResource.PATH)
@Path(TenantResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantResource implements Resource
{

    public static final String PATH = API_ROOT_PATH + "tenant";

    @Inject
    private Execution execution;

    @Inject
    private AccountsService accountsService;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private AddonsRepresentationUnmarshaller addonsRepresentationUnmarshaller;

    @GET
    @Authorized
    @ExistingTenant
    public Response currentTenant()
    {
        return Response.ok(new TenantRepresentation(getGlobalTimeZone(), execution.getContext().getTenant()))
                .build();
    }

    @POST
    @Authorized
    @ExistingTenant
    public Response updateTenant(TenantRepresentation tenantRepresentation)
    {
        Tenant tenant = this.execution.getContext().getTenant();

        tenant.setName(tenantRepresentation.getName());
        tenant.setAddons(addonsRepresentationUnmarshaller.unmarshall(tenantRepresentation.getAddons()));

        try {
            this.accountsService.updateTenant(tenant);
            return Response.ok().build();

        } catch (InvalidEntityException e) {
            return Response.status(422).entity("Invalid entity").build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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
                this.accountsService.updateTenant(updatedTenant);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Tenant not found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    private DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue());
    }
}
