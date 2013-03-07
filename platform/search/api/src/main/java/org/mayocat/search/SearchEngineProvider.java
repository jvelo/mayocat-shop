package org.mayocat.search;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.SearchEngineSettings;
import org.slf4j.Logger;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

public class SearchEngineProvider implements Provider<SearchEngine>
{

    @Inject
    private SearchEngineSettings configuration;

    @Inject
    private ComponentManager componentManager;

    @Inject
    private Logger logger;

    @Override
    public SearchEngine get()
    {
        try {
            return this.componentManager.getInstance(SearchEngine.class, configuration.getName());
        } catch (ComponentLookupException e) {
            try {
                logger.warn("Failed to lookup search engine with name {}. Fallback on default one.",
                    configuration.getName());
                return this.componentManager.getInstance(SearchEngine.class);
            } catch (ComponentLookupException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

}
