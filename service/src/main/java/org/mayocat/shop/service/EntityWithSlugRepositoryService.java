package org.mayocat.shop.service;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.store.StoreException;

public interface EntityWithSlugRepositoryService<T extends Entity> extends EntityRepositoryService<T>
{
    T findBySlug(String slug) throws StoreException;
}
