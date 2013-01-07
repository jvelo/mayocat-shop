package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("ConfigurationResource")
@Path("/configuration/")
@Produces(MediaType.APPLICATION_JSON)
@ExistingTenant
public class ConfigurationResource
{
    @Inject
    private Logger logger;

    @GET
    @Timed
    public Object getConfiguration()
    {
        return null;
    }
}
