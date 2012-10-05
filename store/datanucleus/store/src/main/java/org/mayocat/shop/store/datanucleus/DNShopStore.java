package org.mayocat.shop.store.datanucleus;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.mayocat.shop.model.Shop;
import org.mayocat.shop.model.event.EntityUpdatedEvent;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.ShopStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNShopStore extends AbstractEntityStore<Shop, Long> implements ShopStore
{

    @Override
    public boolean exists(Shop entity) throws StoreException
    {
        return this.findById(entity.getId()) != null;
    }

    @Override
    public void update(Shop entity) throws InvalidEntityException, StoreException
    {
        
        // 3. Persist
        PersistenceManager pm = persistenceManager.get();
        Transaction tx = pm.currentTransaction();
        Shop originalEntity = this.findById(entity.getId());
        try {
            tx.begin();
            originalEntity.setName(entity.getName());
            originalEntity.setProducts(entity.getProducts());
            tx.commit();

            this.observationManager.notify(new EntityUpdatedEvent(), this, entity);

        } catch (JDOException e) {
            this.logger.error("Failed to commit transaction", e);
            throw new StoreException("Failed to commit transaction", e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
}
