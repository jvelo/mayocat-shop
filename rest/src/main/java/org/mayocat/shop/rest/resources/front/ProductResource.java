package org.mayocat.shop.rest.resources.front;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.rest.views.StoreFrontView;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.theme.annotation.Breakpoint;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("FrontProductResource")
@Path("/front/product/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ProductResource implements Resource
{
    @Inject
    private CatalogService catalogService;

    @Path("{slug}")
    @GET
    public StoreFrontView getCategory(@PathParam("slug") String slug,
            @Breakpoint org.mayocat.shop.theme.Breakpoint breakpoint)
    {
        Product product = this.catalogService.findProductBySlug(slug);
        if (product == null) {
            return new StoreFrontView("404", breakpoint);
        }
        return new StoreFrontView("product", breakpoint);
    }
}
