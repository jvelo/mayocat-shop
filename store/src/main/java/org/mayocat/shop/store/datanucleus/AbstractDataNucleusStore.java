package org.mayocat.shop.store.datanucleus;

import javax.inject.Inject;

/**
 * Base class for all datanucleus DAOs.
 */
public class AbstractDataNucleusStore
{
    @Inject
    protected PersistanceManagerFactoryProdiver pmfProvider;

}
