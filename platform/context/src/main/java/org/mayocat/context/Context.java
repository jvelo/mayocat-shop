package org.mayocat.context;

import java.util.Locale;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.session.Session;
import org.mayocat.session.cookies.CookieSession;
import org.mayocat.theme.Theme;

public class Context
{
    private Session session;

    private Tenant tenant;

    private User user;

    private Theme theme;

    private Locale locale;

    private Boolean alternativeLocale;

    private Map<Class, Object> settings = null;

    public Context(Tenant tenant, User user)
    {
        this.tenant = tenant;
        this.user = user;
    }

    public Tenant getTenant()
    {
        return tenant;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        if (this.user != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized user");
        }
        this.user = user;
    }

    public void setTenant(Tenant tenant)
    {
        if (this.tenant != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized tenant");
        }
        this.tenant = tenant;
    }

    public Theme getTheme()
    {
        return theme;
    }

    public void setTheme(Theme theme)
    {
        if (this.theme != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized theme");
        }
        this.theme = theme;
    }

    public void setSettings(Map<Class, Object> settings)
    {
        if (this.settings != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized settings");
        }
        this.settings = settings;
    }

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

    public Locale getLocale()
    {
        if (locale == null) {
            throw new RuntimeException("Illegal attempt at accessing context locale before it is initialized");
        }
        return locale;
    }

    public void setLocale(Locale locale)
    {
        if (this.locale != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized locale");
        }
        this.locale = locale;
    }

    public boolean isAlternativeLocale()
    {
        if (alternativeLocale == null) {
            throw new RuntimeException(
                    "Illegal attempt at accessing context alternative locale flag before it is initialized");
        }
        return alternativeLocale;
    }

    public void setAlternativeLocale(boolean alternativeLocale)
    {
        if (this.alternativeLocale != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized alternative locale flag");
        }
        this.alternativeLocale = alternativeLocale;
    }

    public Session getSession()
    {
        if (session == null) {
            session = new CookieSession();
        }
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }
}
