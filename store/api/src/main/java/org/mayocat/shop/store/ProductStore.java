package org.mayocat.shop.store;

import org.mayocat.shop.model.Product;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore extends Store<Product, Long>
{    
    Product findByHandle(String handle) throws StoreException;
}
