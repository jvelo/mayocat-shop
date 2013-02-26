package org.mayocat.shop.front.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.shop.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;

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
    private FrontBindingManager bindingManager;

    @Inject
    private CatalogService catalogService;

    @GET
    public FrontView getHomePage(@Context Breakpoint breakpoint, @Context UriInfo uriInfo)
    {
        FrontView result = new FrontView("home", breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        List<Product> productList = catalogService.findAllProducts(20, 0);
        List<Map<String, Object>> productsBinding = Lists.newArrayList();
        for (final Product p : productList) {
            productsBinding.add(new HashMap<String, Object>() {{
                put("href", "/product/" + p.getSlug());
                put("title", p.getTitle());
                put("description", p.getDescription());
                put("images", new HashMap<String, Object>() {{
                    put("featured", new HashMap<String, Object>() {{
                        put("theme_small_url", "http://placehold.it/150x150");
                        put("theme_large_url", "http://placehold.it/450x450");
                    }});
                }});
            }});
        }

        bindings.put("products", productsBinding);

        result.putBindings(bindings);

        return result;
    }
}
