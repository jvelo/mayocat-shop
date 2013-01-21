package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface CategoryStore extends Store<Category, Long>, HasOrderedCollections
{
    Category findBySlug(String slug);

    void addProduct(Category category, Product product);

    void removeProduct(Category c, Product p);

    void moveCategory(String categoryToMove, String categoryToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;

    List<Category> findAllForProduct(Product product);
}