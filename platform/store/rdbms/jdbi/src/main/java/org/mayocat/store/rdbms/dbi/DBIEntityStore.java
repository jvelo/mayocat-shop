package org.mayocat.store.rdbms.dbi;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.Execution;
import org.mayocat.store.EntityStore;
import mayoapp.dao.EntityDAO;
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
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(EntityDAO.class);
    }




}