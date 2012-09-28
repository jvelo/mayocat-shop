package org.mayocat.shop.authorization.cookies;

import org.xwiki.component.annotation.Role;

/**
 * A cookie performs reversible encryption and decryption of cookie values.
 * 
 * @version $Id$
 */
@Role
public interface CookieCrypter
{
    /**
     * @param clearText the clear-text value of the cookie to encrypt
     * @return the encrypted value
     * @throws EncryptionException when encryption fails
     */
    String encrypt(String clearText) throws EncryptionException;

    /**
     * @param secret the encrypted version of the cookie, to decrypt
     * @return the decrypted, clear-text version of the cookie value
     * @throws EncryptionException when decryption fails
     */
    String decrypt(String secret) throws EncryptionException;
}
