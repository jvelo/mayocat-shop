package org.mayocat.shop.authorization;

import org.mayocat.shop.model.User;
import org.xwiki.component.annotation.Role;

@Role
public interface Gatekeeper
{
    boolean hasCapability(User user, Capability capability);
}
