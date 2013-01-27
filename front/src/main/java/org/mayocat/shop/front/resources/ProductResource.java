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
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/product/")
@Path("/product/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ProductResource implements Resource
{
    @Inject
    private CatalogService catalogService;

    @Inject
    private EntityFrontViewBuilder viewBuilder;

    @Path("{slug}")
    @GET
    public FrontView getProduct(@PathParam("slug") String slug, @Context Breakpoint breakpoint)
    {
        Product product = this.catalogService.findProductBySlug(slug);
        if (product == null) {
            return viewBuilder.build404(breakpoint);
        }
        return viewBuilder.<Product> buildFrontView(product, breakpoint);
    }
}
