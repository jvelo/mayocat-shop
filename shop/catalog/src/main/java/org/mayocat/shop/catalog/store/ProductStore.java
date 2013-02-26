package org.mayocat.shop.catalog.store;

import java.util.List;

import org.mayocat.shop.catalog.model.Category;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.store.EntityStore;
import org.mayocat.shop.store.HasOrderedCollections;
import org.mayocat.shop.store.InvalidMoveOperation;
import org.mayocat.shop.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore extends Store<Product, Long>, HasOrderedCollections, EntityStore
{    
    Product findBySlug(String slug);

    void moveProduct(String categoryToMove, String categoryToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;

    List<Product> findUncategorizedProducts();

    List<Product> findAllForCategory(Category category);
}
