package org.mayocat.shop.catalog.store.memory;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import org.mayocat.model.Identifiable;
import org.mayocat.model.Slug;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

/**
 * In-memory implementation of {@link ProductStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryProductStore implements ProductStore
{
    private Object lock = new Object();

    /**
     * Wrapper product entity that holds a position so that we can do ordering.
     */
    private final static class PositionedProduct implements Identifiable
    {
        private Product product;

        private Integer position;

        private PositionedProduct(Product product, Integer position)
        {
            this.product = product;
            this.position = position;
        }

        public UUID getId()
        {
            return product.getId();
        }

        public void setId(UUID id)
        {
            product.setId(id);
        }

        private void setProduct(Product product)
        {
            this.product = product;
        }

        private Integer getPosition()
        {
            return position;
        }

        private Product getProduct()
        {
            return product;
        }
    }

    protected Predicate withSlug(final String slug)
    {
        return new Predicate<Product>()
        {
            public boolean apply(@Nullable Product input)
            {
                return input.getSlug().equals(slug);
            }
        };
    }

    private Function<PositionedProduct, Product> positionedToProduct = new Function<PositionedProduct, Product>()
    {
        @Nullable public Product apply(@Nullable PositionedProduct input)
        {
            return input.getProduct();
        }
    };

    private Function<PositionedProduct, Integer> positionOrdering = new Function<PositionedProduct, Integer>()
    {
        public Integer apply(@Nullable PositionedProduct input)
        {
            return input.getPosition();
        }
    };

    private BaseEntityMemoryStore<PositionedProduct> store = new BaseEntityMemoryStore<>();

    private static final Predicate<Product> ON_SHELF = new Predicate<Product>()
    {
        public boolean apply(@Nullable Product input)
        {
            return input.getOnShelf() != null && input.getOnShelf();
        }
    };

    public Product findBySlug(String slug)
    {
        return FluentIterable.from(all()).transform(positionedToProduct).first().orNull();
    }

    public void moveProduct(String productToMove, String productToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        throw new RuntimeException("Unsupported operation exception");
    }

    public List<Product> findOrphanProducts()
    {
        return FluentIterable.from(all()).transform(positionedToProduct).filter(new Predicate<Product>()
        {
            @Override public boolean apply(@Nullable Product input)
            {
                return input.getCollections().isLoaded() && input.getCollections().get().isEmpty();
            }
        }).toList();
    }

    public List<Product> findAllForCollection(final Collection collection)
    {
        return FluentIterable.from(all()).transform(positionedToProduct).filter(new Predicate<Product>()
        {
            public boolean apply(@Nullable Product input)
            {
                return input.getCollections().isLoaded() && input.getCollections().get().contains(collection);
            }
        }).toList();
    }

    public List<Product> findAllOnShelf(Integer number, Integer offset)
    {
        if (number == 0) {
            return FluentIterable.from(all()).transform(positionedToProduct).filter(ON_SHELF).skip(offset).toList();
        }
        return FluentIterable.from(all()).transform(positionedToProduct).filter(ON_SHELF).skip(offset).limit(number)
                .toList();
    }

    private List<PositionedProduct> all()
    {
        return Ordering.natural().onResultOf(positionOrdering).sortedCopy(FluentIterable.from(store.findAll(0, 0)));
    }

    public void updateStock(UUID productId, Integer stockOffset) throws EntityDoesNotExistException
    {
        Product product = this.findById(productId);
        product.setStock(product.getStock() + stockOffset);
    }

    public Product create(@Valid Product entity) throws EntityAlreadyExistsException, InvalidEntityException
    {
        synchronized (lock) {
            return this.store.create(new PositionedProduct(entity, nextPosition())).getProduct();
        }
    }

    private Integer nextPosition()
    {
        List<Integer> positions =
                FluentIterable.from(this.store.findAll(0, 0)).transform(new Function<PositionedProduct, Integer>()
                {
                    @Nullable public Integer apply(@Nullable PositionedProduct input)
                    {
                        return input.getPosition();
                    }
                }).toList();
        return FluentIterable.from(Ordering.natural().reverse().sortedCopy(positions)).first().or(-1) + 1;
    }

    @Override
    public void update(@Valid Product product) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.store.update(forProduct(product));
    }

    private PositionedProduct forProduct(Product product)
    {
        PositionedProduct found = this.store.findById(product.getId());
        found.setProduct(product);
        return found;
    }

    public void delete(@Valid Product entity) throws EntityDoesNotExistException
    {
        this.store.delete(forProduct(entity));
    }

    public Integer countAll()
    {
        return store.countAll();
    }

    public List<Product> findAll(Integer number, Integer offset)
    {
        return FluentIterable.from(store.findAll(number, offset)).transform(positionedToProduct).toList();
    }

    public List<Product> findByIds(List<UUID> ids)
    {
        return FluentIterable.from(store.findByIds(ids)).transform(positionedToProduct).toList();
    }

    @Override
    public Product findById(UUID id)
    {
        return store.findById(id).getProduct();
    }
}
