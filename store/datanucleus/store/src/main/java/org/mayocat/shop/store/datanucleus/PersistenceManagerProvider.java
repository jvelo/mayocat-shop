package org.mayocat.shop.store.datanucleus;

import javax.jdo.PersistenceManager;

import org.xwiki.component.annotation.Role;

@Role
public interface PersistenceManagerProvider
{
    PersistenceManager get();

    void set(PersistenceManager pm);
    
}
