package org.mayocat.shop.authorization.cookies;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.mayocat.shop.configuration.AuthenticationConfiguration;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class DefaultCookieCrypter implements CookieCrypter
{
    
    @Inject
    private AuthenticationConfiguration configuration;
    
    @Inject
    private Logger logger;

    private enum Mode
    {
        CRYPT,
        DECRYPT
    }

    public String encrypt(String clearText) throws EncryptionException
    {
        return this.crypt(clearText, Mode.CRYPT);
    }

    public String decrypt(String secret) throws EncryptionException
    {
        return this.crypt(secret, Mode.DECRYPT);
    }

    private String crypt(String input, Mode mode) throws EncryptionException
    {
        if (Strings.isNullOrEmpty(this.configuration.getCookieEncryptionKey())) {
            throw new EncryptionException("Invalid or missing cookie encryption key in configuration file. " +
            		"You MUST specify a key in order to support cookie authentication.");
        }
        
        try {
            byte[] in = input.getBytes();

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            byte[] keyBytes = this.configuration.getCookieEncryptionKey().getBytes("UTF-8");
            DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
            SecretKey key = keyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            IvParameterSpec spec = null;
            if (cipher.getParameters() != null) {
                spec = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
            }

            switch (mode) {
                case CRYPT:
                default:
                    if (spec != null) {
                        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
                    } else {
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                    }
                    byte[] encrypted = cipher.doFinal(in);
                    return new String(Base64.encodeBase64(encrypted));
                case DECRYPT:
                    if (spec != null) {
                        cipher.init(Cipher.DECRYPT_MODE, key, spec);
                    } else {
                        cipher.init(Cipher.DECRYPT_MODE, key);
                    }
                    byte[] decrypted = cipher.doFinal(Base64.decodeBase64(in));
                    return new String(decrypted);
            }

        } catch (BadPaddingException e) {
            this.logger.warn("Bad padding when attempting to decipher cookies. Key changed ?");
            throw new EncryptionException();
        } catch (Exception e) {
            this.logger.error("Fail to perform cookie crypt or decrypt operation", e);
            throw new EncryptionException(e);
        }
    }
}
