package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNUserStore implements UserStore
{
    @Inject
    protected PersistenceManagerProvider persistanceManagerProvider;
    
    @Override
    public void create(User user) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = persistanceManagerProvider.get();
            pm.makePersistent(user);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }

    @Override
    public User findById(Long id) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = persistanceManagerProvider.get();
            return pm.getObjectById(User.class, id);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } 
    }

    @Override
    public User findByEmailOrUserName(String userNameOrEmail) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManagerProvider.get();

            q = pm.newQuery(User.class);
            q.setFilter("name == param or email == param");
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

    @Override
    public List<User> findAll(int number, int offset) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManagerProvider.get();

            q = pm.newQuery(User.class);
            q.setRange(offset, offset + number);
            return (List<User>) q.execute();
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } 
    }

}
