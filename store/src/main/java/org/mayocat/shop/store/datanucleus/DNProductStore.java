package org.mayocat.shop.store.datanucleus;

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
    protected PersistenceManagerProvider persistanceManagerProvider;

    public void create(@Valid Product product) throws StoreException
    {
        ensureTenantNotTransient(product);

        PersistenceManager pm = null;
        try {
            // // Initial testing for adding to table - works
            //pm = pmfProvider.get();
            pm = persistanceManagerProvider.get();

            //if (this.findByTenantAndHandle(product.getTenant(), product.getHandle()) != null) {
            //    throw new StoreException(MessageFormat.format(
            //        "Product with handle [{0}] already exists for tenant [{1}]", product.getHandle(), product
            //            .getTenant().getHandle()));
            //}
            //Tenant t = pm.getObjectById(Tenant.class, product.getTenant().getId());
            //product.setTenant(t);
            //t.addToProducts(product);
            
            //pm.makePersistent(t);
            pm.makePersistent(product);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
            //    pm.close();
            }
        }
    }

    @Override
    public void update(Product product) throws StoreException
    {
        ensureTenantNotTransient(product);

        PersistenceManager pm = null;
        try {
            // // Initial testing for adding to table - works
            //pm = pmfProvider.get().getPersistenceManager();
            pm = persistanceManagerProvider.get();

            //Product storedProduct = this.findByTenantAndHandle(product.getTenant(), product.getHandle());
            //if (storedProduct == null) {
            //    throw new StoreException(MessageFormat.format("Product with handle [{0}] not found for tenant [{1}]",
            //        product.getHandle(), product.getTenant().getHandle()));
            //}

            //storedProduct.fromProduct(product);
            pm.makePersistent(product);

        } catch (javax.jdo.JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
            //    pm.close();
            }
        }
    }

    public Product findById(Long id) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = persistanceManagerProvider.get();
            return pm.getObjectById(Product.class, id);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } finally {
            if (null != pm) {
            //    pm.close();
            }
        }
    }

    public Product findByTenantAndHandle(Tenant tenant, String handle) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManagerProvider.get();

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
            //    pm.close();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////

    private static void ensureTenantNotTransient(Product product) throws StoreException
    {
        //if (product.getTenant() != null && product.getTenant().getId() == null) {
        //    throw new StoreException(
        //        "Refusing to persist product with transient tenant. Ensure that the product's tenant has been retrieved from database.");
        //}
    }

}
