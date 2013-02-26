package org.mayocat.shop.catalog.store;

import java.util.List;

import org.mayocat.shop.catalog.model.Category;
import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.EntityStore;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface CategoryStore extends Store<Category, Long>, EntityStore, HasOrderedCollections
{
    Category findBySlug(String slug);

    void addProduct(Category category, Product product);

    void removeProduct(Category c, Product p);

    void moveCategory(String categoryToMove, String categoryToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;

    List<EntityAndCount<Category>> findAllWithProductCount();

    List<Category> findAllForProduct(Product product);
}