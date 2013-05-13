package org.mayocat.search.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.context.Execution;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.SearchIndex;
import org.mayocat.model.event.EntityCreatedEvent;
import org.mayocat.model.event.EntityUpdatedEvent;
import org.mayocat.search.EntityIndexSourceMapper;
import org.mayocat.search.SearchEngine;
import org.mayocat.search.SearchEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;

import com.yammer.dropwizard.lifecycle.Managed;

@Component("elasticsearch")
@Singleton
public class ElasticSearchSearchEngine implements SearchEngine, Managed, Initializable
{
    @Inject
    private FilesSettings filesSettings;

    @Inject
    private Logger logger;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private Map<String, EntityIndexSourceMapper> sourceMappers;

    @Inject
    private Execution execution;

    private Client client;

    private class SearchEngineEventListener implements EventListener
    {
        public void onEvent(Event event, Object source, Object data)
        {
            Entity entity = (Entity) source;
            try {
                index(entity);
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
            return Arrays.<Event>asList(new EntityUpdatedEvent(), new EntityCreatedEvent());
        }
    }

    public void initialize() throws InitializationException
    {
        this.observationManager.addListener(new SearchEngineEventListener());
    }

    @Override
    public void index(Entity entity) throws SearchEngineException
    {
        if (execution.getContext().getTenant() == null) {
            throw new SearchEngineException("Cannot index entity : no tenant given and none in execution context");
        }
        index(entity, execution.getContext().getTenant());
    }

    public void index(Entity entity, Tenant tenant) throws SearchEngineException
    {
        try {
            Map<String, Object> source = new HashMap<String, Object>();

            for (EntityIndexSourceMapper mapper : sourceMappers.values()) {
                if (entity.getClass().isAssignableFrom(mapper.forClass())) {
                    source = mapper.mapSource(entity, tenant);
                }
            }

            if (source.keySet().size() > 0) {
                String entityName = StringUtils.substringAfterLast(entity.getClass().getName(), ".").toLowerCase();

                this.logger.debug("Indexing entity {} ...", entityName);

                IndexResponse response =
                        this.client.prepareIndex("entities", entityName, entity.getSlug()).setSource(source).execute()
                                .actionGet();
                this.logger.debug("" + response.type());
            }
        } catch (Exception e) {
            throw new SearchEngineException("Failed to index entity", e);
        }
    }

    @Override
    public List<Map<String, Object>> search(String term, List<Class<? extends Entity>> entityTypes)
            throws SearchEngineException
    {
        SearchResponse response =
                client.prepareSearch("entities").setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(queryString(term))
                        .setFrom(0).setSize(10).setExplain(false).execute().actionGet();

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : response.getHits()) {
            result.add(hit.getSource());
        }
        return result;
    }

    // //////////////////////////////////////////////////////////////////////////////////

    public void start() throws Exception
    {
        try {
            final Builder settings = ImmutableSettings.settingsBuilder();
            settings.put("path.data", filesSettings.getPermanentDirectory());
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
            throw new InitializationException("Failed to initialize embedded elastic search", e);
        }
    }

    public void stop() throws Exception
    {
        this.client.close();
    }
}
