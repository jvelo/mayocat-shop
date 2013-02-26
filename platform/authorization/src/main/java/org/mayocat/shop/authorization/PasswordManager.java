package org.mayocat.shop.authorization;

import org.xwiki.component.annotation.Role;

/**
 * A password manager has the responsibility of hashing passwords and verifying a clear password against an existing
 * hash.
 * 
 * @version $Id$
 */
@Role
public interface PasswordManager
{
    /**
     * @param password the password to hash
     * @return the hashed version of the password
     */
    String hashPassword(String password);

    /**
     * @param candidate the clear-text password to verify
     * @param hashed the knowned valid hashed password to verify against
     * @return true if the hash of the clear-text password matches the known valid hash
     */
    boolean verifyPassword(String candidate, String hashed);
}
