package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNCategoryStore extends AbstractHandleableEntityStore<Category, Long> implements CategoryStore
{

    @Override
    public List<Category> findAllNotSpecial() throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistenceManager.get();

            q = pm.newQuery(Category.class);
            
            q.setFilter("special == false");

            List<Category> results = (List<Category>) q.execute();
            return results;

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }
}
