package org.mayocat.search.elasticsearch;

import javax.inject.Inject;
import javax.inject.Named;

import org.mayocat.lifecycle.Managed;
import org.mayocat.search.SearchEngine;
import org.xwiki.component.annotation.Component;

@Component("elasticsearch")
public class ElasticSearchManaged implements Managed
{

    @Inject
    @Named("elasticsearch")
    private SearchEngine searchEngine;

    @Override
    public void start() throws Exception
    {
        ((ElasticSearchSearchEngine) searchEngine).start();
    }

    @Override
    public void stop() throws Exception
    {
        ((ElasticSearchSearchEngine) searchEngine).stop();
    }

}
