package org.mayocat.context;

import java.util.Map;

import org.mayocat.model.Tenant;
import org.mayocat.model.User;

public class Context
{
    private Tenant tenant;

    private User user;

    private Map<Class, Object> configurations = null;

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

    public void setConfigurations(Map<Class, Object> configurations)
    {
        if (this.configurations != null) {
            throw new RuntimeException("Illegal attempt at replacing already initialized configurations");
        }
        this.configurations = configurations;
    }

    public Object getConfiguration(Class c)
    {
        if (configurations == null) {
            throw new RuntimeException("Illegal attempt at accessing a configuration before they are initialized");
        }
        if (configurations.containsKey(c)) {
            return this.configurations.get(c);
        }
        return null;
    }
}
