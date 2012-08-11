package org.mayocat.shop.store.datanucleus;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNTenantStore extends AbstractHandleableEntityStore<Tenant, Long> implements TenantStore
{
    public Tenant findByHandleOrAlias(String handleOrAlias) throws StoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
