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
import groovy.transform.CompileStatic
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.NoSuchModuleException
import org.mayocat.rest.annotation.ExistingTenant
import org.xwiki.component.annotation.Component

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/configuration")
@Path("/tenant/{tenant}/api/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
@CompileStatic
@ExistingTenant
class TenantConfigurationApi extends ConfigurationApi
{
    @GET
    @Path("settings")
    Map<String, Serializable> getConfiguration()
    {
        return configurationService.settingsAsJson
    }

    @GET
    @Path("settings/{module}")
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
    @Path("settings/{module}")
    @Authorized
    Response updateModuleConfiguration(@PathParam("module") String module, String configurationAsString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper()
            Map<String, Serializable> data = mapper.readValue(configurationAsString,
                    new TypeReference<Map<String, Object>>() {}) as Map<String, Serializable>
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
    @Authorized
    @Path("settings")
    Response updateModuleConfiguration(String configurationAsString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper()
            Map<String, Serializable> data = mapper.readValue(configurationAsString,
                    new TypeReference<Map<String, Object>>() {}) as Map<String, Serializable>
            configurationService.updateSettings(data)
            return Response.noContent().build()
        } catch (IOException e) {
            throw new WebApplicationException(e)
        }
    }
}
