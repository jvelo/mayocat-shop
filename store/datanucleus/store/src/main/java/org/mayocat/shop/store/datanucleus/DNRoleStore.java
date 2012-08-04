package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.authorization.Capability;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.RoleStore;
import org.mayocat.shop.store.StoreException;

public class DNRoleStore implements RoleStore
{
    @Inject
    protected PersistenceManagerProvider persistanceManagerProvider;
    
    
    public void persist(Role role) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = persistanceManagerProvider.get();
            pm.makePersistent(role);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }
    
    public Role findById(Long id) throws StoreException
    {
        PersistenceManager pm = null;
        try {
            pm = persistanceManagerProvider.get();
            return pm.getObjectById(Role.class, id);
        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        } 
    }
    
    public Role findByUserAndCapability(User user, Capability capability) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManagerProvider.get();

            q = pm.newQuery(Product.class);
            q.setFilter("capability in (role.capabilities)");
            q.declareParameters("String capability");

            List<Role> results = (List<Role>) q.execute(capability);
            if (results.size() == 1) {
                return results.get(0);
            }
            return null;

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }
}
