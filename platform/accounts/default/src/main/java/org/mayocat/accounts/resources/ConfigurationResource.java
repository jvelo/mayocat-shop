/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.resources;

import java.io.IOException;
import java.io.Serializable;
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
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.NoSuchModuleException;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.yammer.metrics.annotation.Timed;

@Component(ConfigurationResource.PATH)
@Path(ConfigurationResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
public class ConfigurationResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "configuration";

    @Inject
    private Logger logger;

    @Inject
    private ConfigurationService configurationService;

    @GET
    @Timed
    @Path("settings")
    @ExistingTenant
    public Map<String, Serializable> getConfiguration()
    {
        return configurationService.getSettingsAsJson();
    }

    @GET
    @Timed
    @Path("settings/{module}")
    @ExistingTenant
    public Map<String, Serializable> getModuleConfiguration(@PathParam("module") String module)
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
    @Authorized //(roles = Role.ADMIN)
    @ExistingTenant
    public Response updateModuleConfiguration(@PathParam("module") String module, String configurationAsString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Serializable> data = mapper.readValue(configurationAsString,
                    new TypeReference<Map<String, Object>>()
                    {
                    });
            configurationService.updateSettings(module, data);
            return Response.noContent().build();
        }
        catch (NoSuchModuleException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Timed
    @Authorized //(roles = Role.ADMIN)
    @Path("settings")
    @ExistingTenant
    public Response updateModuleConfiguration(String configurationAsString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Serializable> data = mapper.readValue(configurationAsString,
                    new TypeReference<Map<String, Object>>(){});
            configurationService.updateSettings(data);
            return Response.noContent().build();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

    }

    @GET
    @Timed
    @Path("gestalt")
    public Map<String, Serializable> getGestaltConfiguration()
    {
        return configurationService.getGestaltConfiguration();
    }
}
