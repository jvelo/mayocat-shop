package org.mayocat.shop.store;

import org.mayocat.shop.model.Category;
import org.xwiki.component.annotation.Role;

@Role
public interface CategoryStore extends Store<Category, Long>
{
    Category findByHandle(String handle) throws StoreException;
}
