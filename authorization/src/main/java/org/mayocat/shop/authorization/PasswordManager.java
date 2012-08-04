package org.mayocat.shop.authorization;

import org.xwiki.component.annotation.Role;

@Role
public interface PasswordManager
{
    String hashPassword(String password);
    
    boolean verifyPassword(String candidate, String hashed);
}
