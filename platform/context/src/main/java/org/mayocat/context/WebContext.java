package org.mayocat.context;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.context.scope.Flash;
import org.mayocat.context.scope.Session;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Role;

/**
 * The context of a web request (API request or Front-end request indifferently).
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

    void setSettings(Map<Class, Object> settings);

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
}
