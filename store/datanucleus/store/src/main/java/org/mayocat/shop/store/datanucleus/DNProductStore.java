package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNProductStore extends AbstractDataNucleusStore<Product, Long> implements ProductStore
{
    @Inject
    protected PersistenceManagerProvider persistanceManager;


    public boolean exists(Product entity) throws StoreException
    {
        return this.findByHandle(entity.getHandle()) != null;
    }

    
    public Product findByHandle(String handle) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManager.get();

            q = pm.newQuery(Product.class);
            q.setFilter("handle == handleParam");
            q.declareParameters("String handleParam");

            List<Product> results = (List<Product>) q.execute(handle);
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
        }
    }
}
