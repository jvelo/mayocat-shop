package org.mayocat.shop.search.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.mayocat.shop.base.Managed;
import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.annotation.SearchIndex;
import org.mayocat.shop.model.event.EntityUpdatedEvent;
import org.mayocat.shop.search.SearchEngine;
import org.mayocat.shop.search.SearchEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

@Component(hints = {"elasticsearch", "default"})
public class ElasticSearchSearchEngine implements SearchEngine, Managed, EventListener
{

    @Inject
    private Logger logger;

    private Client client;

    @Override
    public void index(Entity t) throws SearchEngineException
    {
        try {
            Map<String, Object> source = new HashMap<String, Object>();
            for (Field field : t.getClass().getDeclaredFields()) {
                boolean isAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    SearchIndex searchIndex = field.getAnnotation(SearchIndex.class);
                    if (searchIndex != null) {
                        source.put(field.getName(), field.get(t));
                    }
                } finally {
                    field.setAccessible(isAccessible);
                }
            }
            if (source.keySet().size() > 0) {
                String entityName = StringUtils.substringAfterLast(t.getClass().getName(), ".").toLowerCase();

                this.logger.debug("Indexing entity {} ...", entityName);

                // Temporary until we have a generic way of computing an id (handle for example)
                Product p = (Product) t;
                String id = p.getHandle();
                
                IndexResponse response =
                    this.client.prepareIndex("entities", entityName, id).setSource(source).execute().actionGet();
                this.logger.debug("" + response.type());
            }

        } catch (Exception e) {
            throw new SearchEngineException("Failed to index entity", e);
        }
    }

    @Override
    public List<Map<String, Object>> search(String term, List<Class< ? extends Entity>> entityTypes)
        throws SearchEngineException
    {
        SearchResponse response =
            client.prepareSearch("entities").setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryString(term)).setFrom(0).setSize(10).setExplain(false).execute().actionGet();
        
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(); 
        for (SearchHit hit : response.getHits()) {
            result.add(hit.getSource());
        }
        return result;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    
    public void onEvent(Event event, Object source, Object data)
    {
        Entity entity = (Entity) data;
        try {
            this.index(entity);
        } catch (SearchEngineException e) {
            logger.error("Failed to index entity upon update", e);
        }
    }
    
    public String getName()
    {
        return "elasticSearch";
    }

    public List<Event> getEvents()
    {
        return Arrays.<Event> asList(new EntityUpdatedEvent());
    }
    
    ////////////////////////////////////////////////////////////////////////////////////

    public void start() throws Exception
    {
        try {
            final Builder settings = ImmutableSettings.settingsBuilder();
            settings.put("client.transport.sniff", true);
            settings.build();

            final NodeBuilder nb = nodeBuilder().settings(settings).local(true).client(false).data(true);
            final Node node = nb.node();
            client = node.client();

            IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest("entities");

            // we just send back a response, no need to fork a listener
            indicesExistsRequest.listenerThreaded(false);
            client.admin().indices().exists(indicesExistsRequest, new ActionListener<IndicesExistsResponse>()
            {
                public void onResponse(IndicesExistsResponse response)
                {
                    if (!response.exists()) {
                        try {
                            logger.debug("Entities indice does not exists. Creating it...");
                            CreateIndexResponse r =
                                client.admin().indices().create(new CreateIndexRequest("entities")).actionGet();

                            logger.debug("Created indice with response {}", r.acknowledged() ? "\u2713 acknowledged"
                                : "\2718 not acknowledged");
                        } catch (Exception e) {
                            logger.error("Failed to create entities indice status ...");
                        }
                    }
                }

                public void onFailure(Throwable e)
                {
                    logger.error("Failed to enquiry entities indice status ...");
                }
            });

        } catch (Exception e) {
            throw new InitializationException("Failed to initialize embedded solr", e);
        }
    }

    public void stop() throws Exception
    {
        this.client.close();
    }

}
