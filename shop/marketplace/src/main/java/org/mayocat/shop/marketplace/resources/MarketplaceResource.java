package org.mayocat.shop.marketplace.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.mayocat.rest.Resource;
import org.mayocat.search.SearchEngine;
import org.mayocat.search.elasticsearch.ElasticSearchSearchEngine;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

/**
 * @version $Id$
 */
@Path("/marketplace/api/")
@Component("/marketplace/api/")
public class MarketplaceResource implements Resource
{
    @Inject
    private ComponentManager componentManager;

    private ElasticSearchSearchEngine searchEngine;

    @GET
    @Path("products")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts()
    {
        Client client = getSearchEngine().getClient();
        SearchResponse response =
                client.prepareSearch("entities").setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setFrom(0)
                        .setSize(10)
                        .setTypes("product")
                        .setExplain(false)
                        .execute()
                        .actionGet();

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : response.getHits()) {
            result.add(hit.getSource());
        }
        return Response.ok(result).build();
    }

    private ElasticSearchSearchEngine getSearchEngine()
    {
        if (searchEngine == null) {
            try {
                searchEngine =
                        (ElasticSearchSearchEngine) componentManager.getInstance(SearchEngine.class, "elasticsearch");
            } catch (ComponentLookupException e) {
                throw new RuntimeException("Cannot run marketplace API without elasticsearch search engine");
            }
        }
        return searchEngine;
    }
}
