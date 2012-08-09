package org.mayocat.shop.search.elasticsearch;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.annotation.SearchIndex;
import org.mayocat.shop.search.SearchEngine;
import org.mayocat.shop.search.SearchEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = {"solr", "default"})
public class ElasticSearchSearchEngine implements SearchEngine, Initializable
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

                IndexResponse response =
                    this.client.prepareIndex("entities", entityName, "hello").setSource(source).execute().actionGet();
                this.logger.debug("" + response.type());
            }

        } catch (Exception e) {
            throw new SearchEngineException("Failed to index entity", e);
        }
    }

    @Override
    public void initialize() throws InitializationException
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
                @Override
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

                @Override
                public void onFailure(Throwable e)
                {
                    logger.error("Failed to enquiry entities indice status ...");
                }
            });

        } catch (Exception e) {
            throw new InitializationException("Failed to initialize embedded solr", e);
        }

    }

}
