package org.mayocat.shop.authorization;

import javax.inject.Inject;

import org.mayocat.shop.configuration.AuthenticationConfiguration;
import org.mindrot.jbcrypt.BCrypt;
import org.xwiki.component.annotation.Component;

@Component(hints = {"bcrypt", "default"})
public class BCryptPasswordManager implements PasswordManager
{

    @Inject
    private AuthenticationConfiguration configuration;
    
    public String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt(configuration.getPasswordSaltLogRounds()));
    }

    public boolean verifyPassword(String candidate, String hashed)
    {
        return BCrypt.checkpw(candidate, hashed);
    }

}
