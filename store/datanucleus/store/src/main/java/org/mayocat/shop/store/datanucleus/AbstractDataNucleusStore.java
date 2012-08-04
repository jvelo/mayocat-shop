package org.mayocat.shop.store.datanucleus;

import javax.inject.Inject;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.store.Store;

/**
 * Base class for all datanucleus DAOs.
 */
public class AbstractDataNucleusStore<T extends Entity> implements Store<T>
{
    @Inject
    protected PersistenceManagerProvider persistanceManagerProvider;
    
}
