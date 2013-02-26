package org.mayocat.shop.catalog.store.jdbi;

import java.util.List;

import org.mayocat.shop.catalog.model.Category;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.rdbms.dbi.dao.ProductDAO;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.mayocat.store.rdbms.dbi.MoveEntityInListOperation;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = { "jdbi", "default" })
public class DBIProductStore extends DBIEntityStore implements ProductStore, Initializable
{
    private static final String PRODUCT_POSITION = "product.position";

    private static final String PRODUCT_TABLE_NAME = "product";

    private ProductDAO dao;

    public void create(Product product) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(product.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        Long entityId = this.dao.createEntity(product, PRODUCT_TABLE_NAME, getTenant());
        Integer lastIndex = this.dao.lastPosition(getTenant());
        if (lastIndex == null) {
            lastIndex = 0;
        }
        this.dao.create(entityId, lastIndex + 1, product);
        this.dao.insertTranslations(entityId, product.getTranslations());

        this.dao.commit();
    }

    @Override
    public void update(Product product) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        Product originalProduct = this.findBySlug(product.getSlug());
        if (originalProduct == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }
        product.setId(originalProduct.getId());
        Integer updatedRows = this.dao.update(product);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating product");
        }
    }

    public void moveProduct(String productToMove, String productToMoveRelativeTo,
            HasOrderedCollections.RelativePosition relativePosition) throws InvalidMoveOperation
    {
        this.dao.begin();

        List<Product> allProducts = this.findAll();
        MoveEntityInListOperation<Product> moveOp =
                new MoveEntityInListOperation<Product>(allProducts, productToMove,
                        productToMoveRelativeTo, relativePosition);

        if (moveOp.hasMoved()) {
            this.dao.updatePositions(PRODUCT_TABLE_NAME, moveOp.getEntities(), moveOp.getPositions());
        }

        this.dao.commit();
    }

    @Override
    public List<Product> findUncategorizedProducts()
    {
        return this.dao.findUncategorized(getTenant());
    }

    public List<Product> findAll()
    {
        return this.dao.findAll(PRODUCT_TABLE_NAME, PRODUCT_POSITION, getTenant());
    }

    public List<Product> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(PRODUCT_TABLE_NAME, PRODUCT_POSITION, getTenant(), number, offset);
    }

    public Product findBySlug(String slug)
    {
        return this.dao.findBySlugWithTranslations(PRODUCT_TABLE_NAME, slug, getTenant());
    }

    @Override
    public List<Product> findAllForCategory(Category category)
    {
        return this.dao.findAllForCategory(category);
    }

    @Override
    public Product findById(Long id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(ProductDAO.class);
        super.initialize();
    }
}
