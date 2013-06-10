package org.mayocat.manager.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;
import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.meta.TenantEntity;
import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.representations.TenantRepresentation;
import org.mayocat.accounts.representations.UserAndTenantRepresentation;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.authorization.Gatekeeper;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.model.Addon;
import org.mayocat.model.AddonFieldType;
import org.mayocat.model.AddonSource;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.ResultSetRepresentation;
import org.mayocat.rest.support.AddonsRepresentationUnmarshaller;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component(TenantManagerResource.PATH)
@Path(TenantManagerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantManagerResource implements ManagerResource
{
    public static final String PATH = MANAGER_API_ROOT_PATH + TenantEntity.PATH;

    @Inject
    private Execution execution;

    @Inject
    private AccountsService accountsService;

    @Inject
    private Gatekeeper gatekeeper;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private AddonsRepresentationUnmarshaller addonsRepresentationUnmarshaller;

    @GET
    @Path("{slug}")
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    public TenantRepresentation getTenant(@PathParam("slug") String slug)
    {
        Tenant tenant = accountsService.findTenant(slug);
        if (tenant == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return new TenantRepresentation(getGlobalTimeZone(), tenant, PATH + "/" + tenant.getSlug());
    }

    @GET
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    public ResultSetRepresentation<TenantRepresentation> getAllTenants(
            @QueryParam("number") @DefaultValue("50") Integer limit,
            @DefaultValue("0") @QueryParam("offset") Integer offset)
    {
        List<Tenant> tenants = accountsService.findAllTenants(limit, offset);
        if (tenants == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<TenantRepresentation> representations = new ArrayList<TenantRepresentation>();
        for (Tenant tenant : tenants) {

            representations.add(new TenantRepresentation(getGlobalTimeZone(), tenant,
                    PATH + "/" + tenant.getSlug()));
        }

        Integer total = this.accountsService.countAllTenants();
        ResultSetRepresentation<TenantRepresentation> result = new ResultSetRepresentation<TenantRepresentation>(
                PATH + "/",
                limit,
                offset,
                representations,
                total
        );

        return result;
    }

    @POST
    @Path("{slug}")
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    public Response updateTenant(@PathParam("slug") String slug, TenantRepresentation tenantRepresentation)
    {
        Tenant tenant = accountsService.findTenant(slug);

        tenant.setName(tenantRepresentation.getName());
        tenant.setAddons(addonsRepresentationUnmarshaller.unmarshall(tenantRepresentation.getAddons(), true));

        try {
            this.accountsService.updateTenant(tenant);
            return Response.ok().build();
        } catch (InvalidEntityException e) {
            return Response.status(422).entity("Invalid entity").build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createTenant(@Valid UserAndTenantRepresentation userAndTenant)
    {
        if (!multitenancySettings.isActivated()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Tenant creation is not allowed on this server\n")
                    .build();
        }

        if (!isTenantCreationAllowed()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try {
            Tenant tenant = new Tenant();
            TenantRepresentation tenantRepresentation = userAndTenant.getTenant();
            tenant.setSlug(tenantRepresentation.getSlug());
            tenant.setName(tenantRepresentation.getName());
            tenant.setAddons(addonsRepresentationUnmarshaller.unmarshall(tenantRepresentation.getAddons()));
            tenant.setCreationDate(new Date());
            accountsService.createTenant(tenant);
            execution.getContext().setTenant(tenant);
            accountsService.createInitialUser(userAndTenant.getUser());
            return Response.ok().build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity("A tenant with this slug already exists").build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private boolean isTenantCreationAllowed()
    {
        if (multitenancySettings.getRequiredRoleForTenantCreation() != Role.NONE) {

            User contextUser = execution.getContext().getUser();
            if (contextUser == null || !contextUser.isGlobal()) {
                return false;
            }

            return gatekeeper.userHasRole(execution.getContext().getUser(),
                    multitenancySettings.getRequiredRoleForTenantCreation());
        } else {
            return true;
        }
    }

    private DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue());
    }
}
