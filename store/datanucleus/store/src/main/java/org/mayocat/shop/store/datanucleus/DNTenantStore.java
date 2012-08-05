package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNTenantStore extends AbstractDataNucleusStore<Tenant, Long> implements TenantStore
{

    public boolean exists(Tenant entity) throws StoreException
    {
        return this.findByHandle(entity.getHandle()) != null;
    }
    
    public Tenant findByHandle(String handle) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManager.get();

            q = pm.newQuery(Tenant.class);
            q.setFilter("handle == handleParam");
            q.declareParameters("String handleParam");

            List<Tenant> results = (List<Tenant>) q.execute(handle);
            if (results.size() > 0) {
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
               // pm.close();
            }
        }
    }
    
    public Tenant findByHandleOrAlias(String handleOrAlias) throws StoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
