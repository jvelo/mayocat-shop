package org.mayocat.shop.model;

import java.util.HashSet;
import java.util.Set;

import org.mayocat.shop.authorization.Capability;

public class Role extends Entity
{
    public enum RoleName
    {
        ADMIN,
        MANAGER,
        DESIGNER,
        ACCOUNTANT
    }

    Long id;

    RoleName name;

    Set<Capability> capabilities;

    public Role()
    {
        
    }
    
    public RoleName getName()
    {
        return name;
    }

    public void setName(RoleName name)
    {
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    
    public void addToCapabilities(Capability capability)
    {
        if (this.capabilities == null) {
            this.capabilities = new HashSet<Capability>();
        }
        this.capabilities.add(capability);
    }

    public void removeFromCapabilities(Capability capability)
    {
        this.capabilities.remove(capability);
    }

}
