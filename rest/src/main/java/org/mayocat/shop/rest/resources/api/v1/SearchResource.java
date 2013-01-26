package org.mayocat.shop.rest.resources.api.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.search.SearchEngine;
import org.mayocat.shop.search.SearchEngineException;
import org.xwiki.component.annotation.Component;

@Component("/api/1.0/search/")
@Path("/api/1.0/search/")
@Produces(MediaType.APPLICATION_JSON)
@ExistingTenant
public class SearchResource implements Resource
{

    @Inject
    private Provider<SearchEngine> searchEngine;

    @GET
    @Authorized
    public List<Map<String, Object>> search(@QueryParam("term") String term)
    {
        List<Class< ? extends Entity>> classes = new ArrayList<Class< ? extends Entity>>();
        classes.add(Product.class);

        try {
            return this.searchEngine.get().search(term, classes);
        } catch (SearchEngineException e) {
            throw new WebApplicationException();
        }
    }

}
