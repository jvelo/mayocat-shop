package org.mayocat.authorization;

import org.mayocat.accounts.model.User;
import org.xwiki.component.annotation.Role;

@Role
public interface Gatekeeper
{
    boolean userHasRole(User user, org.mayocat.accounts.model.Role role);
}
