/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
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
import org.mayocat.context.WebContext;
import org.mayocat.model.Entity;
import org.mayocat.model.event.EntityCreatedEvent;
import org.mayocat.model.event.EntityUpdatedEvent;
import org.mayocat.search.EntityIndexDocumentPurveyor;
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

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

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
    private EntityIndexDocumentPurveyor entityIndexDocumentPurveyor;

    @Inject
    private WebContext context;

    @Inject
    private Map<String, EntityMappingGenerator> mappingGenerators;

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
        if (context.getTenant() == null) {
            throw new SearchEngineException("Cannot index entity : no tenant given and none in context context");
        }
        index(entity, context.getTenant());
    }

    public void index(final Entity entity, final Tenant tenant) throws SearchEngineException
    {
        try {
            Map<String, Object> source = entityIndexDocumentPurveyor.purveyDocument(entity, tenant);

            if (source.keySet().size() > 0) {
                String entityName = StringUtils.substringAfterLast(entity.getClass().getName(), ".").toLowerCase();

                this.logger.debug("Indexing {} with id {}...", entityName, entity.getId());

                IndexRequestBuilder builder =
                        this.client.prepareIndex("entities", entityName, entity.getId().toString()).setSource(source);

                if (tenant != null && !Tenant.class.isAssignableFrom(entity.getClass())) {
                    builder.setParent(tenant.getId().toString());
                }

                IndexResponse response = builder.execute().actionGet();
                this.logger.debug("" + response.getType());
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
        Executors.newSingleThreadExecutor().submit(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                try {
                    final Builder settings = ImmutableSettings.settingsBuilder();
                    settings.put("path.data", filesSettings.getPermanentDirectory().toString());
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
                            if (!response.isExists()) {
                                try {
                                    logger.debug("Entities indice does not exists. Creating it...");
                                    CreateIndexResponse r =
                                            client.admin().indices().create(new CreateIndexRequest("entities"))
                                                    .actionGet();

                                    logger.debug("Created indice with response {}",
                                            r.isAcknowledged() ? "\u2713 acknowledged"
                                                    : "\2718 not acknowledged");
                                } catch (Exception e) {
                                    logger.error("Failed to create entities indice status ...");
                                }
                            }

                            updateMappings();
                        }

                        public void onFailure(Throwable e)
                        {
                            logger.error("Failed to enquiry entities indice status ...", e);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Failed to initialize embedded elastic search", e);
                }
                return null;
            }
        });
    }

    private void updateMappings()
    {
        for (String entity : mappingGenerators.keySet()) {
            EntityMappingGenerator generator = mappingGenerators.get(entity);
            Map<String, Object> mapping = generator.generateMapping();
            if (mapping != null) {
                final String type = generator.forClass().getSimpleName().toLowerCase();
                ListenableActionFuture<PutMappingResponse> future = client.admin().indices()
                        .preparePutMapping("entities")
                        .setType(type)
                        .setSource(mapping)
                        .execute();

                future.addListener(new ActionListener<PutMappingResponse>()
                {
                    public void onResponse(PutMappingResponse putMappingResponse)
                    {
                        logger.info("Mapping for entity [{}] updated", type);
                    }

                    public void onFailure(Throwable throwable)
                    {
                        logger.error("Error updating mapping for entity {}", type,  throwable);
                    }
                });

                future.actionGet();
            }
        }
    }

    public void stop() throws Exception
    {
        this.client.close();
    }

    public Client getClient()
    {
        return client;
    }
}
