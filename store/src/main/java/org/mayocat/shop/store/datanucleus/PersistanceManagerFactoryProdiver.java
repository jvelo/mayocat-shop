package org.mayocat.shop.store.datanucleus;

import javax.jdo.PersistenceManagerFactory;

import org.xwiki.component.annotation.Role;

@Role
public interface PersistanceManagerFactoryProdiver
{
    PersistenceManagerFactory get();

}
