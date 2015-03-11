/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context.internal;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.context.WebContext;
import org.mayocat.context.request.WebRequest;
import org.mayocat.context.scope.Flash;
import org.mayocat.context.scope.Session;
import org.mayocat.context.scope.cookie.CookieFlash;
import org.mayocat.context.scope.cookie.CookieSession;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;

import com.google.common.base.Optional;

/**
 * Default implementation of {@link WebContext}
 *
 * @version $Id$
 */
public class DefaultWebContext implements WebContext
{
    private Flash flash;

    private Session session;

    private Tenant tenant;

    private User user;

    private Theme theme;

    private Locale locale;

    private Boolean alternativeLocale;

    private Map<Class, Serializable> settings = null;

    private WebRequest webRequest;

    public DefaultWebContext(Tenant tenant, User user)
    {
        this.tenant = tenant;
        this.user = user;
    }

    @Override
    public Tenant getTenant()
    {
        return tenant;
    }

    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public void setUser(User user)
    {
        if (this.user != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized user");
        }
        this.user = user;
    }

    @Override
    public void setTenant(Tenant tenant)
    {
        if (this.tenant != null) {
            // FIXME check how we can handle tenant + user creation with this restriction
            //throw new RuntimeException("Illegal attempt at replacing already initialized tenant");
        }
        this.tenant = tenant;
    }

    @Override
    public Theme getTheme()
    {
        return theme;
    }

    @Override
    public void setTheme(Theme theme)
    {
        if (this.theme != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized theme");
        }
        this.theme = theme;
    }

    @Override
    public void setSettings(Map<Class, Serializable> settings)
    {
        if (this.settings != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized settings");
        }
        this.settings = settings;
    }

    @Override
    public <T extends ExposedSettings> T getSettings(Class<T> c)
    {
        if (settings == null) {
            throw new RuntimeException("Illegal attempt at accessing a configuration before they are initialized");
        }
        if (settings.containsKey(c)) {
            return (T) this.settings.get(c);
        }
        return null;
    }

    @Override
    public Locale getLocale()
    {
        if (locale == null) {
            throw new RuntimeException("Illegal attempt at accessing context locale before it is initialized");
        }
        return locale;
    }

    @Override
    public void setLocale(Locale locale)
    {
        if (this.locale != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized locale");
        }
        this.locale = locale;
    }

    @Override
    public boolean isAlternativeLocale()
    {
        if (alternativeLocale == null) {
            throw new RuntimeException(
                    "Illegal attempt at accessing context alternative locale flag before it is initialized");
        }
        return alternativeLocale;
    }

    @Override
    public void setAlternativeLocale(boolean alternativeLocale)
    {
        if (this.alternativeLocale != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized alternative locale flag");
        }
        this.alternativeLocale = alternativeLocale;
    }

    @Override
    public Session getSession()
    {
        if (session == null) {
            session = new CookieSession();
        }
        return session;
    }

    @Override
    public void setSession(Session session)
    {
        this.session = session;
    }

    @Override
    public void setFlash(Flash flash)
    {
        this.flash = flash;
    }

    @Override
    public Flash getFlash()
    {
        if (flash == null) {
            flash = new CookieFlash();
        }
        return flash;
    }

    @Override
    public void flash(String name, Serializable value)
    {
        getFlash().setAttribute(name, value);
    }

    @Override
    public void session(String name, Serializable value)
    {
        getSession().setAttribute(name, value);
    }

    @Override
    public WebRequest getRequest()
    {
        if (webRequest == null) {
            throw new NullPointerException("Web request has not been initialized");
        }
        return webRequest;
    }

    @Override
    public void setRequest(WebRequest webRequest)
    {
        if (this.webRequest != null) {
            throw new RuntimeException("Illegal attempt at modifying the web request that has already been set");
        }
        this.webRequest = webRequest;
    }
}
