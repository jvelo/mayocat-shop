package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.validation.Valid;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class ProductStore implements org.mayocat.shop.store.ProductStore
{
    @Inject
    protected PersistanceManagerFactoryProdiver pmfProvider;
    
    /*
    public ProductStore()
    {
        
    }
    
    public ProductStore(PersistanceManagerFactoryProdiver pmfProvider)
    {
        this.pmfProvider = pmfProvider;
    }
    */
    
    public void setPersistanceManagerFactoryProdiver(PersistanceManagerFactoryProdiver pmfProvider) {
        this.pmfProvider = pmfProvider;
    }

    public void persist(String tenant, @Valid Product p) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            // // Initial testing for adding to table - works
            pm = pmfProvider.get().getPersistenceManager();

            // javax.jdo.Transaction transaction = pm.currentTransaction();
            // transaction.begin();

            p.setTenant(tenant);
            pm.makePersistent(p);
            // transaction.commit();

        } catch (javax.jdo.JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
                pm.close();
            }
        }
    }

    public Product getProduct(Long id) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = pmfProvider.get().getPersistenceManager();
            return pm.getObjectById(Product.class, id);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
                pm.close();
            }
        }
    }

    public Product getProduct(String tenant, String handle) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = pmfProvider.get().getPersistenceManager();

            q = pm.newQuery(Product.class);
            q.setFilter("handle == handleParam && tenant == tenantParam");
            q.declareParameters("String handleParam, String tenantParam");

            List<Product> results = (List<Product>) q.execute(handle, tenant);
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

}
