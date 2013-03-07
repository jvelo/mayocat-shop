package org.mayocat.context;

import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.base.ExposedSettings;
import org.mayocat.theme.Theme;

public class Context
{
    private Tenant tenant;

    private User user;

    private Theme theme;

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
            throw new RuntimeException("Illegal attempt at replacing already initialized the");
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
}
