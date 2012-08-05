package org.mayocat.shop.authorization;

import org.mindrot.jbcrypt.BCrypt;
import org.xwiki.component.annotation.Component;

@Component(hints = {"bcrypt", "default"})
public class BCryptPasswordManager implements PasswordManager
{

    public String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String candidate, String hashed)
    {
        return BCrypt.checkpw(candidate, hashed);
    }

}
