package org.mayocat.shop.store.datanucleus;

import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.model.UserRole;
import org.mayocat.shop.store.RoleStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNRoleStore extends AbstractEntityStore<Role, Long> implements RoleStore
{
    public List<Role> findAllByUser(User user) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistenceManager.get();
            
            // Select all roles from UserRole that contains the passed user
            //q = pm.newQuery(Role.class, "(select ur.role from org.mayocat.shop.model.UserRole ur).contains(user)");
            q = pm.newQuery(UserRole.class);
            q.setResult("role");
            q.setFilter("user == userParam");
            q.declareParameters("User userParam");
            List<Role> results = (List<Role>) q.execute(user);
            return results;

        } catch (JDODataStoreException e) {
            throw new StoreException(e);
        }
    }

    @Override
    public boolean exists(Role entity) throws StoreException
    {
        return false;
    }

    @Override
    public void update(Role entity) throws StoreException
    {
        throw new RuntimeException("Not implemented.");
    }

}
