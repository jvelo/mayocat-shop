package org.mayocat.shop.store;

import org.mayocat.shop.model.Product;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore
{
    void persist(String tenant, Product p) throws StoreException;
    
    Product getProduct(Long id) throws StoreException;
    
    Product getProduct(String tenant, String handle) throws StoreException;
}
