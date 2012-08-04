package org.mayocat.shop.authorization;

import org.mayocat.shop.model.User;
import org.xwiki.component.annotation.Role;

@Role
public interface Authenticator
{
    boolean respondTo(String headerName, String headerValue);
    
    User verify(String value);
}
