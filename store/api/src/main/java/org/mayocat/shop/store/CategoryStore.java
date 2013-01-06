package org.mayocat.shop.store;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface CategoryStore extends Store<Category, Long>
{
    Category findBySlug(String slug, Tenant t);
}
