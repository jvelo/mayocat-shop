package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.xwiki.component.annotation.Role;

@Role
public interface CategoryStore extends Store<Category, Long>
{
    Category findBySlug(String slug) throws StoreException;
    
    List<Category> findAllNotSpecial() throws StoreException;
}
