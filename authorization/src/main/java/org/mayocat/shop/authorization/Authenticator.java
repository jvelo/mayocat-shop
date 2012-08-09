package org.mayocat.shop.authorization;

import org.mayocat.shop.model.User;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

@Role
public interface Authenticator
{
    boolean respondTo(String headerName, String headerValue);
    
    Optional<User> verify(String value);
}
