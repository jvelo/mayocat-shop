package org.mayocat.shop.service;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantService extends EntityRepositoryService<Tenant>
{
    Tenant findBySlugOrAlias(String tenantOrAlias) throws StoreException;

    void create(Tenant tenant) throws InvalidEntityException, EntityAlreadyExistsException, StoreException;
    
    void update(Tenant tenant) throws InvalidEntityException, StoreException;
}
