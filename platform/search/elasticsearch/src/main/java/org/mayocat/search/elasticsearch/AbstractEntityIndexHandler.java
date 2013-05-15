package org.mayocat.search.elasticsearch;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.Execution;
import org.mayocat.model.Entity;
import org.mayocat.search.EntityIndexHandler;
import org.slf4j.Logger;
import org.xwiki.component.manager.ComponentManager;

/**
 * @version $Id$
 */
public abstract class AbstractEntityIndexHandler implements EntityIndexHandler
{
    public AbstractEntityIndexHandler()
    {
    }

    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Inject
    private EntitySourceExtractor extractor;

    @Inject
    private ComponentManager componentManager;

    @Override
    public Map<String, Object> getDocument(Entity entity)
    {
        return this.getDocument(entity, execution.getContext().getTenant());
    }

    @Override
    public void updateMapping()
    {
        // Nothing
    }

    protected Map<String, Object> extractSourceFromEntity(Entity entity, Tenant tenant)
    {
        return extractor.extract(entity, tenant);
    }
}
