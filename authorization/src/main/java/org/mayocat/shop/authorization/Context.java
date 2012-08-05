package org.mayocat.shop.authorization;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;

public class Context
{
    
    private Tenant tenant;
    private User user;

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
    
    
}
