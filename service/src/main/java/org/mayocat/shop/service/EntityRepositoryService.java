package org.mayocat.shop.service;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.store.StoreException;

public interface EntityRepositoryService<T extends Entity>
{
    T findBySlug(String slug) throws StoreException;
}
