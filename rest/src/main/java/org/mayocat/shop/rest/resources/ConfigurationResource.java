package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("ConfigurationResource")
@Path("/configuration/")
public class ConfigurationResource
{

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public Object getConfiguration()
    {
        return null;
    }

}
