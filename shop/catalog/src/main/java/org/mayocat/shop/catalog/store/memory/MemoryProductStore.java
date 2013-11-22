package org.mayocat.shop.catalog.store.memory;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.memory.BaseEntityMemoryStore;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
public class MemoryProductStore extends BaseEntityMemoryStore<Product> implements ProductStore
{
    @Override
    public Product findBySlug(String slug)
    {
        return FluentIterable.from(entities.values()).filter(withSlug(slug)).limit(1).first().orNull();
    }

    @Override public void moveProduct(String productToMove, String productToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        // TODO
    }

    @Override
    public List<Product> findOrphanProducts()
    {
        return FluentIterable.from(entities.values()).filter(new Predicate<Product>()
        {
            @Override public boolean apply(@Nullable Product input)
            {
                return input.getCollections().get().isEmpty();
            }
        }).toList();
    }

    @Override public List<Product> findAllForCollection(Collection collection)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public List<Product> findAllOnShelf(Integer number, Integer offset)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void updateStock(UUID productId, Integer stockOffset) throws EntityDoesNotExistException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
