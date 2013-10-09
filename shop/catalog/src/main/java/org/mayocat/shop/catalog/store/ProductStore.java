package org.mayocat.shop.catalog.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.EntityStore;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore extends Store<Product, UUID>, HasOrderedCollections, EntityStore
{    
    Product findBySlug(String slug);

    void moveProduct(String productToMove, String productToMoveRelativeTo, RelativePosition relativePosition)
            throws InvalidMoveOperation;

    List<Product> findOrphanProducts();

    List<Product> findAllForCollection(Collection collection);

    List<Product> findAllOnShelf(Integer number, Integer offset);
}
