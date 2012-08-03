package org.mayocat.shop.model;

import java.util.Set;

import org.mayocat.shop.authorization.Capability;

public class Role
{
    Long id;
    
    String name;
    
    Set<Capability> capabilities;

    public void addToCapabilities(Capability capability)
    {
        this.capabilities.add(capability);
    }

    public void removeFromCapabilities(Capability capability)
    {
        this.capabilities.remove(capability);
    }
}
