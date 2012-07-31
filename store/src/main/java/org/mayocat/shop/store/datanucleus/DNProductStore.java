package org.mayocat.shop.store.datanucleus;

import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.validation.Valid;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNProductStore implements org.mayocat.shop.store.ProductStore
{
    @Inject
    protected PersistanceManagerFactoryProdiver pmfProvider;

    public void create(@Valid Product p) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            // // Initial testing for adding to table - works
            pm = pmfProvider.get().getPersistenceManager();
            
            if (this.findByTenantAndHandle(p.getTenant(), p.getHandle()) != null) {
                throw new StoreException(
                    MessageFormat.format("Product with handle [{0}] already exists for tenant [{1}]", p.getHandle(), p.getTenant().getHandle()));
            }

            // javax.jdo.Transaction transaction = pm.currentTransaction();
            // transaction.begin();

            pm.makePersistent(p);
            // transaction.commit();

        } finally {
            if (null != pm) {
                pm.close();
            }
        }
    }
    
    @Override
    public void update(Product p) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            // // Initial testing for adding to table - works
            pm = pmfProvider.get().getPersistenceManager();
            
            Product storedProduct = this.findByTenantAndHandle(p.getTenant(), p.getHandle());
            if (storedProduct == null) {
                throw new StoreException(
                    MessageFormat.format("Product with handle [{0}] not found for tenant [{1}]", p.getHandle(), p.getTenant().getHandle()));
            }
            
            storedProduct.fromProduct(p);
            pm.makePersistent(storedProduct);

        } catch (javax.jdo.JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
                pm.close();
            }
        }
        
    }

    public Product findById(Long id) throws StoreException
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

    public Product findByTenantAndHandle(Tenant tenant, String handle) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = pmfProvider.get().getPersistenceManager();

            q = pm.newQuery(Product.class);
            q.setFilter("handle == handleParam && tenant.id == tenantParam");
            q.declareParameters("String handleParam, Long tenantParam");

            List<Product> results = (List<Product>) q.execute(handle, tenant.getId());
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
