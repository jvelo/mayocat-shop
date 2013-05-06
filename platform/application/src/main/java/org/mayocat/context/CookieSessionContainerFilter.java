package org.mayocat.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.mayocat.configuration.SecuritySettings;
import org.mayocat.security.Cipher;
import org.mayocat.security.EncryptionException;
import org.mayocat.session.Session;
import org.mayocat.session.cookies.CookieSession;
import org.mayocat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * @version $Id$
 */
public class CookieSessionContainerFilter implements ContainerResponseFilter, ContainerRequestFilter
{
    private final static String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieSessionContainerFilter.class);

    private static final String SESSION_COOKIE_NAME = "session";

    // TODO: make configurable ?
    private static final int SESSION_DURATION = 60 * 60 * 24 * 15;

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse)
    {
        Execution execution = Utils.getComponent(Execution.class);
        if (execution.getContext() != null && sessionExistsAndIsNotEmpty(execution)) {
            storeSessionInCookie(execution, containerResponse);
        }
        return containerResponse;
    }

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        Session session = null;
        Execution execution = Utils.getComponent(Execution.class);
        if (execution.getContext() != null) {
            try {
                session = getSessionFromCookie(containerRequest);
                if (session != null) {
                    execution.getContext().setSession(session);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to get session from cookies", e);
            } catch (EncryptionException e) {
                LOGGER.error("Failed to get session from cookies", e);
            }
        }
        return containerRequest;
    }

    private Session getSessionFromCookie(ContainerRequest containerRequest) throws IOException, EncryptionException
    {
        Cipher cipher = Utils.getComponent(Cipher.class);
        String cookieData = decode(getSessionCookie(containerRequest));

        if (null != cookieData) {
            try {
                String encryptedCookie = cookieData.substring(0, cookieData.length() - 44);
                String signature = cookieData.substring(cookieData.length() - 44);
                if (!signature.equals(computeSignature(encryptedCookie))) {
                    // Signing key has been changed or message has been tampered with
                    LOGGER.warn("Invalid HMAC signature when reading session from cookies");
                    return null;
                }

                String decrypted = cipher.decrypt(encryptedCookie);
                CookieSession cookieSession = (CookieSession) deSerialize(decrypted);

                return cookieSession;
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to de-serialize session from cookies", e);
            } catch (GeneralSecurityException e) {
                LOGGER.error("Failed to de-serialize session from cookies", e);
            }
        }
        return null;
    }

    private boolean sessionExistsAndIsNotEmpty(Execution execution)
    {
        return execution.getContext() != null && execution.getContext().getSession() != null &&
                !execution.getContext().getSession().isEmpty();
    }

    private void storeSessionInCookie(Execution execution, ContainerResponse containerResponse)
    {
        Cipher cipher = Utils.getComponent(Cipher.class);
        Session cookieSession = execution.getContext().getSession();

        try {
            String serialized = serialize(cookieSession);
            String encryptedData = cipher.encrypt(serialized);
            String signature = computeSignature(encryptedData);

            Session originalSession = getSessionFromCookie(containerResponse.getContainerRequest());
            if (originalSession != null && originalSession.equals(cookieSession)) {
                return;
            }

            NewCookie session =
                    new NewCookie("session", encode(encryptedData + signature), "/", null, 1, "", SESSION_DURATION, false);
            Response cookieResponse =
                    Response.fromResponse(containerResponse.getResponse()).cookie(session).build();

            containerResponse.setResponse(cookieResponse);
        } catch (IOException e) {
            LOGGER.error("Failed to store cookies in session", e);
        } catch (EncryptionException e) {
            LOGGER.error("Failed to store cookies in session", e);
        } catch (GeneralSecurityException e) {
            LOGGER.error("Failed to store cookies in session", e);
        }
    }

    private String encode(String s)
    {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String decode(String s)
    {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    private String computeSignature(String message) throws GeneralSecurityException
    {
        SecuritySettings securitySettings = Utils.getComponent(SecuritySettings.class);
        SecretKeySpec signingKey =
                new SecretKeySpec(securitySettings.getSigningKey().getBytes(), HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes(Charsets.UTF_8));
        String result = new String(Base64.encode(rawHmac));
        return result;
    }

    private String getSessionCookie(ContainerRequest request)
    {
        MultivaluedMap<String, String> cookies = request.getCookieNameValueMap();
        if (cookies != null) {
            for (String name : cookies.keySet()) {
                if (SESSION_COOKIE_NAME.equals(name)) {
                    return cookies.getFirst(name);
                }
            }
        }
        return null;
    }

    private Object deSerialize(String session) throws IOException, ClassNotFoundException
    {
        byte[] data = Base64.decode(session);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    private String serialize(Serializable object) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return new String(Base64.encode(byteArrayOutputStream.toByteArray()));
    }
}
