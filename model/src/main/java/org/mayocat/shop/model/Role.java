package org.mayocat.shop.model;

import java.util.Set;

import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

import org.mayocat.shop.authorization.Capability;

@PersistenceCapable(table = "role", detachable = "true")
@Uniques({@Unique(name = "UNIQUE_NAME", members = {"name"})})
public class Role
{
    String name;
    
    @Join
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
