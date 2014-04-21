package org.mayocat.accounts.passwords;

import org.xwiki.component.annotation.Role;

/**
 * A password strength checker verifies that passwords match security strength requirements.
 *
 * @version $Id$
 */
@Role
public interface PasswordStrengthChecker
{
    boolean isStrongEnough(String password);
}
