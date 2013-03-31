package org.mayocat.security;

import javax.inject.Inject;

import org.mayocat.configuration.SecuritySettings;
import org.mindrot.jbcrypt.BCrypt;
import org.xwiki.component.annotation.Component;

@Component(hints = {"bcrypt", "default"})
public class BCryptPasswordManager implements PasswordManager
{

    @Inject
    private SecuritySettings configuration;
    
    public String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt(configuration.getPasswordSaltLogRounds()));
    }

    public boolean verifyPassword(String candidate, String hashed)
    {
        return BCrypt.checkpw(candidate, hashed);
    }

}
