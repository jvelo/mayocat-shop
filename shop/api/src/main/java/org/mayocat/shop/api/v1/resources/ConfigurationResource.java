package org.mayocat.shop.api.v1.resources;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.context.Execution;
import org.mayocat.accounts.model.Role;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.base.Resource;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.NoSuchModuleException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("/api/1.0/configuration/")
@Path("/api/1.0/configuration/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@Authorized
public class ConfigurationResource implements Resource
{
    @Inject
    private Logger logger;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Execution execution;

    @GET
    @Timed
    @Path("settings")
    public Map<String, Object> getConfiguration()
    {
        return configurationService.getSettingsAsJson();
    }

    @GET
    @Timed
    @Path("settings/{module}")
    public Map<String, Object> getModuleConfiguration(@PathParam("module") String module)
    {
        try {
            return configurationService.getSettingsAsJson(module);
        } catch (NoSuchModuleException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    @PUT
    @Timed
    @Path("settings/{module}")
    @Authorized(roles = Role.ADMIN)
    public Response updateModuleConfiguration(@PathParam("module") String module, Map<String, Object> configuration)
    {
        try {
            configurationService.updateSettings(module, configuration);
            return Response.noContent().build();
        }
        catch (NoSuchModuleException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @GET
    @Timed
    @Path("gestalt")
    public Map<String, Object> getGestaltConfiguration()
    {
        return configurationService.getGestaltConfiguration();
    }

    @PUT
    @Timed
    @Authorized(roles = Role.ADMIN)
    public Response updateModuleConfiguration(Map<String, Object> configuration)
    {
        configurationService.updateSettings(configuration);
        return Response.noContent().build();
    }
}
