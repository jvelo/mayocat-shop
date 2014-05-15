/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.yammer.metrics.annotation.Timed
import groovy.transform.CompileStatic
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.NoSuchModuleException
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/configuration")
@Path("/api/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@CompileStatic
class ConfigurationApi implements Resource
{
    @Inject
    ConfigurationService configurationService

    @GET
    @Timed
    @Path("settings")
    @ExistingTenant
    Map<String, Serializable> getConfiguration()
    {
        return configurationService.settingsAsJson
    }

    @GET
    @Timed
    @Path("settings/{module}")
    @ExistingTenant
    Map<String, Serializable> getModuleConfiguration(@PathParam("module") String module)
    {
        try {
            return configurationService.getSettingsAsJson(module)
        } catch (NoSuchModuleException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build())
        }
    }

    @PUT
    @Timed
    @Path("settings/{module}")
    @Authorized //(roles = Role.ADMIN)
    @ExistingTenant
    Response updateModuleConfiguration(@PathParam("module") String module, String configurationAsString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper()
            Map<String, Serializable> data = mapper.readValue(configurationAsString,
                    new TypeReference<Map<String, Object>>(){}) as Map<String, Serializable>
            configurationService.updateSettings(module, data)
            return Response.noContent().build()
        }
        catch (NoSuchModuleException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        } catch (IOException e) {
            throw new WebApplicationException(e)
        }
    }

    @PUT
    @Timed
    @Authorized //(roles = Role.ADMIN)
    @Path("settings")
    @ExistingTenant
    Response updateModuleConfiguration(String configurationAsString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper()
            Map<String, Serializable> data = mapper.readValue(configurationAsString,
                    new TypeReference<Map<String, Object>>(){}) as Map<String, Serializable>
            configurationService.updateSettings(data)
            return Response.noContent().build()
        } catch (IOException e) {
            throw new WebApplicationException(e)
        }

    }

    @GET
    @Timed
    @Path("gestalt")
    Map<String, Serializable> getGestaltConfiguration()
    {
        return configurationService.getGestaltConfiguration()
    }
}
