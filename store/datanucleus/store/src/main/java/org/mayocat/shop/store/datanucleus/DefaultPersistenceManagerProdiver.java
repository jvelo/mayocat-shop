package org.mayocat.shop.store.datanucleus;

import javax.inject.Singleton;
import javax.jdo.PersistenceManager;

import org.xwiki.component.annotation.Component;

@Component
@Singleton
public class DefaultPersistenceManagerProdiver implements PersistenceManagerProvider
{
    private ThreadLocal<PersistenceManager> pm = new ThreadLocal<PersistenceManager>();
    
    public PersistenceManager get()
    {
        return this.pm.get();
    }
    
    public void set(PersistenceManager pm)
    {
        this.pm.set(pm);
    }
    
}
