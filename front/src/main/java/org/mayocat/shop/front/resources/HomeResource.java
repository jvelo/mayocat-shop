package org.mayocat.shop.front.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.mayocat.shop.front.EntityFrontViewBuilder;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.shop.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/")
@Path("/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class HomeResource implements Resource
{
    @Inject
    private EntityFrontViewBuilder viewBuilder;

    @GET
    public FrontView getProduct(@Context Breakpoint breakpoint)
    {
        return viewBuilder.buildFrontView("home", breakpoint);
    }
}
