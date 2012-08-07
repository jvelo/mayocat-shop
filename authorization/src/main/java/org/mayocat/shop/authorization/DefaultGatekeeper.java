package org.mayocat.shop.authorization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.RoleStore;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultGatekeeper implements Gatekeeper
{

    @Inject
    private Provider<RoleStore> roleStore;

    @Inject
    private Logger logger;

    @Override
    public boolean hasCapability(User user, Class< ? extends Capability> capability)
    {
        try {
            String name = capability.newInstance().getName();
            List<Role> userRoles = this.roleStore.get().findAllByUser(user);
            for (Role role : userRoles) {
                for (Capability cap : role.getCapabilities()) {
                    if (cap.getName().equals(name)) {
                        return true;
                    }
                }
            }
            // Pas cap
            return false;

        } catch (StoreException e) {
            this.logger.error("Failed to find role for user and capability", e);
        } catch (InstantiationException e) {
            this.logger.error("Failed to find role for user and capability", e);
        } catch (IllegalAccessException e) {
            this.logger.error("Failed to find role for user and capability", e);
        }
        return false;
    }

}
