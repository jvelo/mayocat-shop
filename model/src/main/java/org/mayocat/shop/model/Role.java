package org.mayocat.shop.model;

import java.util.Set;

import org.mayocat.shop.authorization.Capability;

public class Role extends Entity
{
    Long id;
    
    String name;
    
    Set<Capability> capabilities;
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void addToCapabilities(Capability capability)
    {
        this.capabilities.add(capability);
    }

    public void removeFromCapabilities(Capability capability)
    {
        this.capabilities.remove(capability);
    }
}
