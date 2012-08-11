package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.UserRole;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserRoleStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNUserRoleStore extends AbstractEntityStore<UserRole, Long> implements UserRoleStore
{

    public boolean exists(UserRole entity) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistenceManager.get();

            q = pm.newQuery(UserRole.class);
            q.setFilter("user==userParam && role==roleParam");
            q.declareParameters("User userParam, Role roleParam");

            List<Role> results = (List<Role>) q.execute(entity.getUser(), entity.getRole());
            return (results.size()> 0);

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }

    @Override
    public void update(UserRole entity) throws StoreException
    {
        throw new RuntimeException("Not implemented");
    }
    
}
