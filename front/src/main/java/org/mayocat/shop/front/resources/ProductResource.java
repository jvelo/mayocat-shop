package org.mayocat.shop.front.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.shop.front.bindings.BindingsContants;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("/product/")
@Path("/product/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ProductResource implements Resource, BindingsContants
{
    @Inject
    private CatalogService catalogService;

    @Inject
    private FrontBindingManager bindingManager;

    @Path("{slug}")
    @GET
    public FrontView getProduct(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        Product product = this.catalogService.findProductBySlug(slug);
        if (product == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("product", breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        bindings.put(PAGE_TITLE, product.getTitle());
        bindings.put(PAGE_DESCRIPTION, product.getDescription());

        // TODO Introduce a notion of "Front representation"
        Map<String, Object> productContext = Maps.newHashMap();
        productContext.put("title", product.getTitle());
        productContext.put("description", product.getDescription());
        productContext.put("images", new HashMap<String, Object>()
        {{
            put("featured", new HashMap<String, Object>() {{
                put("theme_small_url", "http://placehold.it/150x150");
                put("theme_large_url", "http://placehold.it/450x450");
            }});
            put("all", new ArrayList<Map<String, Object>>(){{
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
            }});
        }});

        bindings.put("product", productContext);
        result.putBindings(bindings);

        return result;
    }
}
