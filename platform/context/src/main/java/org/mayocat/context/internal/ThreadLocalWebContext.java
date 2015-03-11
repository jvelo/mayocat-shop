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

import javax.inject.Singleton;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.context.WebContext;
import org.mayocat.context.request.WebRequest;
import org.mayocat.context.scope.Flash;
import org.mayocat.context.scope.Session;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

@Component
@Singleton
public class ThreadLocalWebContext implements WebContext
{
    private ThreadLocal<WebContext> context = new ThreadLocal<>();

    public WebContext getContext()
    {
        return this.context.get();
    }

    public void setContext(DefaultWebContext context)
    {
        this.context.set(context);
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Wrapped methods

    @Override
    public Tenant getTenant()
    {
        return getContext().getTenant();
    }

    @Override
    public User getUser()
    {
        return getContext().getUser();
    }

    @Override
    public void setUser(User user)
    {
        getContext().setUser(user);
    }

    @Override
    public void setTenant(Tenant tenant)
    {
        getContext().setTenant(tenant);
    }

    @Override
    public Theme getTheme()
    {
        return getContext().getTheme();
    }

    @Override
    public void setTheme(Theme theme)
    {
        getContext().setTheme(theme);
    }

    @Override
    public void setSettings(Map<Class, Serializable> settings)
    {
        getContext().setSettings(settings);
    }

    @Override
    public <T extends ExposedSettings> T getSettings(Class<T> c)
    {
        return getContext().getSettings(c);
    }

    @Override
    public Locale getLocale()
    {
        return getContext().getLocale();
    }

    @Override
    public void setLocale(Locale locale)
    {
        getContext().setLocale(locale);
    }

    @Override
    public boolean isAlternativeLocale()
    {
        return getContext().isAlternativeLocale();
    }

    @Override
    public void setAlternativeLocale(boolean alternativeLocale)
    {
        getContext().setAlternativeLocale(alternativeLocale);
    }

    @Override
    public Session getSession()
    {
        return getContext().getSession();
    }

    @Override
    public void setSession(Session session)
    {
        getContext().setSession(session);
    }

    @Override
    public void setFlash(Flash flash)
    {
        getContext().setFlash(flash);
    }

    @Override
    public Flash getFlash()
    {
        return getContext().getFlash();
    }

    @Override
    public void flash(String name, Serializable value)
    {
        getContext().flash(name, value);
    }

    @Override
    public void session(String name, Serializable value)
    {
        getContext().session(name, value);
    }

    @Override
    public WebRequest getRequest()
    {
        return getContext().getRequest();
    }

    @Override
    public void setRequest(WebRequest request)
    {
        this.getContext().setRequest(request);
    }
}
