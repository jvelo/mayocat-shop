package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.User;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNUserStore extends AbstractHandleableEntityStore<User, Long> implements UserStore
{
    public boolean exists(User entity) throws StoreException
    {
        return this.findByEmailOrUserName(entity.getEmail()) != null;
    }

    public User findByEmailOrUserName(String userNameOrEmail) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistenceManager.get();

            q = pm.newQuery(User.class);
            q.setFilter("email == param");
            q.declareParameters("String param");

            List<User> results = (List<User>) q.execute(userNameOrEmail);
            if (results.size() == 1) {
                return results.get(0);
            }
            return null;

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }

    public List<User> findAll(int number, int offset) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistenceManager.get();

            q = pm.newQuery(User.class);
            q.setRange(offset, offset + number);
            return (List<User>) q.execute();
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }

}
