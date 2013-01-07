package org.mayocat.shop.store.rdbms.dbi;

import javax.inject.Inject;

import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Tenant;

public abstract class AbstractEntityStore
{
    @Inject
    private Execution execution;

    public AbstractEntityStore()
    {
    }

    protected Tenant getTenant()
    {
        return this.execution.getContext().getTenant();
    }
}