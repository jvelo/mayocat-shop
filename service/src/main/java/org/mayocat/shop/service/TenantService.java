package org.mayocat.shop.service;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantService extends EntityWithHandleRepositoryService<Tenant>
{
    Tenant findByHandleOrAlias(String tenantOrAlias) throws StoreException;
}
