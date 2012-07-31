package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNTenantStore implements TenantStore
{

    @Inject
    protected PersistanceManagerFactoryProdiver pmfProvider;

    public void setPersistanceManagerFactoryProdiver(PersistanceManagerFactoryProdiver pmfProvider)
    {
        this.pmfProvider = pmfProvider;
    }

    @Override
    public void create(Tenant t) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            // // Initial testing for adding to table - works
            pm = pmfProvider.get().getPersistenceManager();
            Transaction transaction = pm.currentTransaction();

            try {
                transaction.begin();
                pm.makePersistent(t);
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                    throw new StoreException("Failed to commit transaction.");
                }
            }
        } finally {
            if (null != pm) {
                pm.close();
            }
        }
    }

    @Override
    public void update(Tenant t) throws StoreException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Tenant findById(Long id) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = pmfProvider.get().getPersistenceManager();
            return pm.getObjectById(Tenant.class, id);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
                pm.close();
            }
        }
    }

    @Override
    public Tenant findByHandle(String handle) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = pmfProvider.get().getPersistenceManager();

            q = pm.newQuery(Tenant.class);
            q.setFilter("handle == handleParam");
            q.declareParameters("String handleParam");

            List<Tenant> results = (List<Tenant>) q.execute(handle);
            if (results.size() == 1) {
                return results.get(0);
            }
            return null;

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != q) {
                q.closeAll();
            }
            if (null != pm) {
                pm.close();
            }
        }
    }

    @Override
    public Tenant findByHandleOrAlias(String handleOrAlias) throws StoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
