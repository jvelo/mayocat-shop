package org.mayocat.shop.store.datanucleus;

import org.mayocat.shop.model.Shop;
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
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
