/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
import org.mayocat.context.scope.WebScope;
import org.mayocat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Base class for cookie-based scope container filters such as {@link SessionScopeCookieContainerFilter} or {@link
 * FlashScopeCookieContainerFilter}
 *
 * @version $Id$
 */
public abstract class AbstractScopeCookieContainerFilter<T extends WebScope>
        implements ContainerResponseFilter, ContainerRequestFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScopeCookieContainerFilter.class);

    protected abstract String getScopeAndCookieName();

    protected abstract boolean scopeExistsAndNotEmpty(WebContext context);

    protected abstract T getScope(WebContext context);

    protected abstract void setScope(WebContext context, T scope);

    protected abstract boolean encryptAndSign();

    protected abstract T cast(Object object);

    protected int getCookieDuration()
    {
        return -1;
    }

    private final static String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse)
    {
        WebContext context = Utils.getComponent(WebContext.class);
        if (context != null && scopeExistsAndNotEmpty(context)) {
            storeScopeInCookies(context, containerResponse);
        }
        else {
            deleteCookie(containerResponse);
        }
        return containerResponse;
    }

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        T scope = null;
        WebContext context = Utils.getComponent(WebContext.class);
        try {
            scope = getScopeFromCookies(containerRequest);
            if (scope != null) {
                setScope(context, scope);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get {} from cookies", getScopeAndCookieName(), e);
        } catch (EncryptionException e) {
            LOGGER.error("Failed to get {} from cookies", getScopeAndCookieName(), e);
        }
        return containerRequest;
    }

    protected void deleteCookie(ContainerResponse containerResponse)
    {
        // Set the cookie with an expiration date in the past
        containerResponse.getHttpHeaders().add("Set-Cookie",
                getScopeAndCookieName() + "=deleted;Path=/;Expires=Thu, 01-Jan-1970 00:00:01 GMT");
    }

    protected void storeScopeInCookies(WebContext context, ContainerResponse containerResponse)
    {
        Cipher cipher = Utils.getComponent(Cipher.class);
        T cookieScope = getScope(context);

        try {
            String cookieValue;
            String serialized = serialize(cookieScope);

            if (encryptAndSign()) {
                String encryptedData = cipher.encrypt(serialized);
                String signature = computeSignature(encryptedData);
                cookieValue = encode(encryptedData + signature);
            } else {
                cookieValue = encode(serialized);
            }

            try {
                T originalScope = getScopeFromCookies(containerResponse.getContainerRequest());
                if (originalScope != null && originalScope.equals(cookieScope)) {
                    return;
                }
            } catch (IOException e) {
                // Keep going.
                // It may mean a serialized class has changed and could not be loaded
                // This means me have a new scope and it's OK to override it
            }

            NewCookie scope =
                    new NewCookie(getScopeAndCookieName(), cookieValue, "/", null, 1, "",
                            getCookieDuration(), false);
            Response cookieResponse =
                    Response.fromResponse(containerResponse.getResponse()).cookie(scope).build();

            containerResponse.setResponse(cookieResponse);
        } catch (IOException | EncryptionException | GeneralSecurityException e) {
            LOGGER.error("Failed to store cookies in {}", getScopeAndCookieName(), e);
        }
    }

    protected T getScopeFromCookies(ContainerRequest containerRequest) throws IOException, EncryptionException
    {
        Cipher cipher = Utils.getComponent(Cipher.class);
        String cookieData = decode(getCookie(containerRequest, getScopeAndCookieName()));

        if (!Strings.isNullOrEmpty(cookieData)) {
            try {
                if (encryptAndSign()) {
                    if (cookieData.length() < 44) {
                        LOGGER.warn("Invalid length when reading {} from cookies", getScopeAndCookieName());
                        return null;
                    }
                    String encryptedCookie = cookieData.substring(0, cookieData.length() - 44);
                    String signature = cookieData.substring(cookieData.length() - 44);
                    if (!signature.equals(computeSignature(encryptedCookie))) {
                        // Signing key has been changed or message has been tampered with
                        LOGGER.warn("Invalid HMAC signature when reading {} from cookies", getScopeAndCookieName());
                        return null;
                    }
                    cookieData = cipher.decrypt(encryptedCookie);
                }

                T scope = cast(deserialize(cookieData));
                return scope;
            } catch (GeneralSecurityException | ClassNotFoundException e) {
                LOGGER.error("Failed to de-serialize {} from cookies", getScopeAndCookieName(), e);
            } catch (ClassCastException e2) {
                // Ignore and return null -> scope will be destroyed
            }
        }
        return null;
    }

    protected Object deserialize(String serialized) throws IOException, ClassNotFoundException
    {
        byte[] data = Base64.decode(serialized);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    protected String serialize(Serializable object) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return new String(Base64.encode(byteArrayOutputStream.toByteArray()));
    }

    protected String getCookie(ContainerRequest request, String cookieName)
    {
        MultivaluedMap<String, String> cookies = request.getCookieNameValueMap();
        if (cookies != null) {
            for (String name : cookies.keySet()) {
                if (cookieName.equals(name)) {
                    return cookies.getFirst(name);
                }
            }
        }
        return null;
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

    private String encode(String s)
    {
        if (s == null) {
            // garbage in, garbage out
            return null;
        }
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String decode(String s)
    {
        if (s == null) {
            // garbage in, garbage out
            return null;
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
