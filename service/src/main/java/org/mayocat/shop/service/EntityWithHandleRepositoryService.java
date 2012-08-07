package org.mayocat.shop.service;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.store.StoreException;

public interface EntityWithHandleRepositoryService<T extends Entity> extends EntityRepositoryService<T>
{
    T findByHandle(String handle) throws StoreException;
}
