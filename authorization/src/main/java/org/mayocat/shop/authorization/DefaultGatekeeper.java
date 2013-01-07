package org.mayocat.shop.authorization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultGatekeeper implements Gatekeeper
{

    @Inject
    private Provider<UserStore> userStore;

    @Inject
    private Logger logger;

    @Override
    public boolean hasCapability(User user, Class< ? extends Capability> capability)
    {
        try {
            String name = capability.newInstance().getName();
            List<Role> userRoles = this.userStore.get().findRolesForUser(user);
            for (Role role : userRoles) {
                // FIXME
                return true;
            }
            // Pas cap
            return false;

        } catch (InstantiationException e) {
            this.logger.error("Failed to find role for user and capability", e);
        } catch (IllegalAccessException e) {
            this.logger.error("Failed to find role for user and capability", e);
        }
        return false;
    }

}
