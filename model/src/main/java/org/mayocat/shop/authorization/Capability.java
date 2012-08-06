package org.mayocat.shop.authorization;

import org.xwiki.component.annotation.Role;

public class Capability
{    
    String name;
    
    public Capability(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String toString()
    {
        return this.name;
    }
}
