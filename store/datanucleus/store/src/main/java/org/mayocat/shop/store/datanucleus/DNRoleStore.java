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
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNRoleStore extends AbstractDataNucleusStore<Role, Long> implements RoleStore
{
    @Inject
    protected PersistenceManagerProvider persistanceManager;

    public Role findByUserAndCapability(User user, Capability capability) throws StoreException
    {
        PersistenceManager pm = null;
        Query q = null;
        try {
            pm = persistanceManager.get();

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

    @Override
    public boolean exists(Role entity) throws StoreException
    {
        return false;
    }

}
