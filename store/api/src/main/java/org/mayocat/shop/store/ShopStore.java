package org.mayocat.shop.store;

import org.mayocat.shop.model.Shop;
import org.xwiki.component.annotation.Role;

@Role
public interface ShopStore extends Store<Shop, Long>
{
}
