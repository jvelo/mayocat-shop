package org.mayocat.shop.rest.resources;

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

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.context.Context;
import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.search.SearchEngine;
import org.mayocat.shop.search.SearchEngineException;
import org.xwiki.component.annotation.Component;

@Component("SearchResource")
@Path("/search/")
public class SearchResource implements Resource
{

    @Inject
    private Provider<SearchEngine> searchEngine;

    @GET
    @Produces({"application/json; charset=UTF-8"})
    public List<Map<String, Object>> search(@Authorized Context context, @QueryParam("term") String term)
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
