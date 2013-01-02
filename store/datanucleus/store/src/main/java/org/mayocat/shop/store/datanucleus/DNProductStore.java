package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNProductStore extends AbstractEntityWithSlugStore<Product, Long> implements ProductStore
{

    @Override
    public List<Product> findAllInCategory(Category category, int number, int offset) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistenceManager.get();

            q = pm.newQuery(Product.class);
            
            q.setFilter("categories.contains(categoryParam)");
            q.declareParameters("Category categoryParam");

            List<Product> results = (List<Product>) q.execute(category);
            return results;

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }
}
