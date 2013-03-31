package org.mayocat.rest.resources;

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

import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.model.Entity;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.search.SearchEngine;
import org.mayocat.search.SearchEngineException;
import org.xwiki.component.annotation.Component;

@Component(SearchResource.PATH)
@Path(SearchResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@ExistingTenant
public class SearchResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "search";

    @Inject
    private Provider<SearchEngine> searchEngine;

    @GET
    @Authorized
    public List<Map<String, Object>> search(@QueryParam("term") String term)
    {
        List<Class<? extends Entity>> classes = new ArrayList<Class<? extends Entity>>();

        try {
            return this.searchEngine.get().search(term, classes);
        } catch (SearchEngineException e) {
            throw new WebApplicationException();
        }
    }
}
