package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
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
