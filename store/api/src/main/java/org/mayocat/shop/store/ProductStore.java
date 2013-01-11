package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore extends Store<Product, Long>, HasOrderedCollections
{    
    Product findBySlug(String slug);
    
    List<Product> findAllInCategory(Category category, int number, int offset);

    void moveProduct(String categoryToMove, String categoryToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;
}
