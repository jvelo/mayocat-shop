package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore extends Store<Product, Long>
{    
    Product findByHandle(String handle) throws StoreException;
    
    List<Product> findAllInCategory(Category category, int number, int offset) throws StoreException;
}
