package org.mayocat.shop.authorization.cookies;

import org.xwiki.component.annotation.Role;

@Role
public interface CookieCrypter
{
    String encrypt(String clearText) throws EncryptionException;
    
    String decrypt(String secret) throws EncryptionException;
}
