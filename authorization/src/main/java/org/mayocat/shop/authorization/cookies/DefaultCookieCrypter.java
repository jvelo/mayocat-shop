package org.mayocat.shop.authorization.cookies;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultCookieCrypter implements CookieCrypter
{

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
        try {
            byte[] in = input.getBytes();

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            byte[] keyBytes = "1234567890azertyuiopqsdf".getBytes("UTF-8");
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

        } catch (InvalidParameterSpecException e) {
            this.logger.error("Failed to get cipher", e);
            throw new EncryptionException(e);
        } catch (NoSuchAlgorithmException e) {
            this.logger.error("Failed to get cipher", e);
            throw new EncryptionException(e);
        } catch (NoSuchPaddingException e) {
            this.logger.error("Failed to get cipher", e);
            throw new EncryptionException(e);
        } catch (InvalidKeyException e) {
            this.logger.error("Failed to get cipher", e);
            throw new EncryptionException();
        } catch (InvalidAlgorithmParameterException e) {
            this.logger.error("Failed to get cipher", e);
            throw new EncryptionException(e);
        } catch (InvalidKeySpecException e) {
            this.logger.error("Invalid key", e);
            throw new EncryptionException(e);
        } catch (IllegalBlockSizeException e) {
            this.logger.error("Failed to encrypt", e);
            throw new EncryptionException(e);
        } catch (BadPaddingException e) {
            this.logger.error("Failed to encrypt", e);
            throw new EncryptionException();
        } catch (UnsupportedEncodingException e) {
            this.logger.error("Failed to encrypt", e);
            throw new EncryptionException(e);
        }
    }
}
