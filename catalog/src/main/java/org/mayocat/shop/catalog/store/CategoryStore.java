package org.mayocat.shop.catalog.store;

import java.util.List;

import org.mayocat.shop.catalog.model.Category;
import org.mayocat.shop.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.store.EntityStore;
import org.mayocat.shop.store.HasOrderedCollections;
import org.mayocat.shop.store.InvalidMoveOperation;
import org.mayocat.shop.store.Store;
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