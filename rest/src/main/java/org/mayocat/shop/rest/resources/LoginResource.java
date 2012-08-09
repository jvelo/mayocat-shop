package org.mayocat.shop.rest.resources;

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
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.mayocat.shop.authorization.PasswordManager;
import org.mayocat.shop.model.User;
import org.mayocat.shop.service.UserService;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component("LoginResource")
@Path("/login/")
public class LoginResource implements Resource
{

    @Inject
    private UserService userService;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private Logger logger;

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password,
        @QueryParam("remember") @DefaultValue("false") Boolean remember, @Context UriInfo uri)
    {
        try {
            User user = userService.findByEmailOrUserName(username);

            if (user == null) {
                // Don't give more information than this
                return Response.status(Status.UNAUTHORIZED).build();
            }

            if (!passwordManager.verifyPassword(password, user.getPassword())) {
                // Don't give more information than this
                return Response.status(Status.UNAUTHORIZED).build();
            }

            // Find out some cookie parameters we will need
            String domain = uri.getBaseUri().getHost();
            int ageWhenRemember = 60 * 60 * 24 * 15; // TODO make configurable

            // Create the new cookies to be sent with the response
            NewCookie newUserCookie =
                new NewCookie("username", crypt(username, Mode.CRYPT), "/", domain, "",
                    remember ? ageWhenRemember : -1, false);
            NewCookie newPassCookie =
                new NewCookie("password", crypt(password, Mode.CRYPT), "/", domain, "",
                    remember ? ageWhenRemember : -1, false);

            return Response.ok().cookie(newUserCookie, newPassCookie).build();

        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    private enum Mode
    {
        CRYPT,
        DECRYPT
    }

    private String crypt(String input, Mode mode)
    {
        try {
            byte[] in = input.getBytes();

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            byte[] keyBytes = "Hello fdsa fsda ".getBytes();
            DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
            SecretKey key = keyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
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
            throw new WebApplicationException();
        } catch (NoSuchAlgorithmException e) {
            this.logger.error("Failed to get cipher", e);
            throw new WebApplicationException();
        } catch (NoSuchPaddingException e) {
            this.logger.error("Failed to get cipher", e);
            throw new WebApplicationException();
        } catch (InvalidKeyException e) {
            this.logger.error("Failed to get cipher", e);
            throw new WebApplicationException();
        } catch (InvalidAlgorithmParameterException e) {
            this.logger.error("Failed to get cipher", e);
            throw new WebApplicationException();
        } catch (InvalidKeySpecException e) {
            this.logger.error("Invalid key", e);
            throw new WebApplicationException();
        } catch (IllegalBlockSizeException e) {
            this.logger.error("Failed to encrypt", e);
            throw new WebApplicationException();
        } catch (BadPaddingException e) {
            this.logger.error("Failed to encrypt", e);
            throw new WebApplicationException();
        }
    }
}
