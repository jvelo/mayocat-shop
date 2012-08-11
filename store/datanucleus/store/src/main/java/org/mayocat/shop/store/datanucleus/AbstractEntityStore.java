package org.mayocat.shop.store.datanucleus;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.event.EntityUpdatedEvent;
import org.mayocat.shop.model.event.EntityCreatedEvent;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.Store;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.observation.ObservationManager;

/**
 * Base class for all datanucleus DAOs.
 */
public abstract class AbstractEntityStore<T extends Entity, K extends Serializable> implements Store<T, K>
{

    private Class<T> type;

    @Inject
    protected PersistenceManagerProvider persistenceManager;
    
    @Inject
    protected Logger logger;

    @Inject
    protected ObservationManager observationManager;
    
    public AbstractEntityStore()
    {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract boolean exists(T entity) throws StoreException;

    public void create(T entity) throws EntityAlreadyExistsException, StoreException
    {
        if (this.exists(entity)) {
            throw new EntityAlreadyExistsException(MessageFormat.format("Entity [{0}] already exists", entity));
        }
        PersistenceManager pm = persistenceManager.get();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            pm.makePersistent(entity);
            tx.commit();

            //this.observationManager.notify(new EntityCreatedEvent(), this, entity);
            
        } catch (JDOException e) {
            this.logger.error("Failed to commit transaction", e);
            throw new StoreException("Failed to commit transaction", e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    public abstract void update(T entity) throws StoreException;

    public T findById(K id) throws StoreException
    {
        PersistenceManager pm = persistenceManager.get();
        try {
            return pm.getObjectById(this.type, id);
        } catch (JDODataStoreException e) {
            throw new StoreException(MessageFormat.format("Failed to obtain entity of type [{0}] by its id [{1}]",
                this.type, id), e);
        }
    }

    protected Class<T> getPersistentType()
    {
        return this.type;
    }


}
