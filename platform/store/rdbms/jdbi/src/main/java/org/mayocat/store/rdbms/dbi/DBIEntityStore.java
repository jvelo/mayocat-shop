package org.mayocat.store.rdbms.dbi;

import javax.inject.Inject;

import org.mayocat.context.Execution;
import org.mayocat.model.Tenant;
import org.mayocat.model.reference.EntityReference;
import org.mayocat.store.EntityStore;
import org.mayocat.store.rdbms.dbi.dao.EntityDAO;
import org.skife.jdbi.v2.DBI;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = { "jdbi", "default" })
public class DBIEntityStore implements EntityStore, Initializable
{
    @Inject
    private Execution execution;

    @Inject
    private DBIProvider dbi;

    private EntityDAO dao;

    public DBIEntityStore()
    {
    }

    public DBI getDbi()
    {
        return dbi.get();
    }

    protected Tenant getTenant()
    {
        return this.execution.getContext().getTenant();
    }

    @Override
    public EntityReference getReference(Long id)
    {
        return this.dao.getEntity(id);
    }

    @Override
    public Long getId(EntityReference reference)
    {
        return this.dao.getId(reference, getTenant());
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(EntityDAO.class);
    }
}