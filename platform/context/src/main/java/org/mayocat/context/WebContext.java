/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.context.request.WebRequest;
import org.mayocat.context.scope.Flash;
import org.mayocat.context.scope.Session;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * The context data relevant to a mayocat web exchange (API request or Front-end  web request indifferently)
 *
 * @version $Id$
 */
@Role
public interface WebContext
{
    Tenant getTenant();

    User getUser();

    void setUser(User user);

    void setTenant(Tenant tenant);

    Theme getTheme();

    void setTheme(Theme theme);

    void setSettings(Map<Class, Serializable> settings);

    <T extends ExposedSettings> T getSettings(Class<T> c);

    Locale getLocale();

    void setLocale(Locale locale);

    boolean isAlternativeLocale();

    void setAlternativeLocale(boolean alternativeLocale);

    Session getSession();

    void setSession(Session session);

    void setFlash(Flash flash);

    Flash getFlash();

    void flash(String name, Serializable value);

    void session(String name, Serializable value);

    WebRequest getRequest();

    void setRequest(WebRequest request);
}
