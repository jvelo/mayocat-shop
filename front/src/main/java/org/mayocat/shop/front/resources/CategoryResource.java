package org.mayocat.shop.front.resources;

import java.util.HashMap;
import java.util.List;
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
import org.mayocat.shop.model.Category;
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
@Component("/category/")
@Path("/category/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CategoryResource implements Resource, BindingsContants
{
    @Inject
    private CatalogService catalogService;

    @Inject
    private FrontBindingManager bindingManager;

    @Path("{slug}")
    @GET
    public FrontView getCategory(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        Category category = catalogService.findCategoryBySlug(slug);
        if (category == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("category", breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        bindings.put(PAGE_TITLE, category.getTitle());
        bindings.put(PAGE_DESCRIPTION, category.getDescription());

        // Sets the "current" flag on the current category
        try {
            List<Map<String, Object>> categories = (List<Map<String, Object>>) bindings.get(CATEGORIES);
            for (Map<String, Object> c : categories) {
                if (c.containsKey("url") && c.get("url").equals("/category/" + category.getSlug())) {
                    c.put("current", true);
                }
            }
        }
        catch (ClassCastException e) {
            // Ignore
        }

        // TODO Introduce a notion of "Front representation"
        Map<String, Object> categoryContext = Maps.newHashMap();
        categoryContext.put("title", category.getTitle());
        categoryContext.put("description", category.getDescription());

        bindings.put("product", categoryContext);
        result.putBindings(bindings);

        return result;
    }
}
