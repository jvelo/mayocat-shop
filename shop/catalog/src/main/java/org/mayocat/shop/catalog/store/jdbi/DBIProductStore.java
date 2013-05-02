package org.mayocat.shop.catalog.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.mayocat.model.Addon;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.AttachmentStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.mayocat.store.rdbms.dbi.MoveEntityInListOperation;
import mayoapp.dao.ProductDAO;
import org.mayocat.store.rdbms.jdbi.AddonsHelper;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints = { "jdbi", "default" })
public class DBIProductStore extends DBIEntityStore implements ProductStore, Initializable
{
    private static final String PRODUCT_POSITION = "product.position";

    private static final String PRODUCT_TABLE_NAME = "product";

    private ProductDAO dao;

    @Inject
    private AttachmentStore attachmentStore;

    public Product create(Product product) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(product.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        product.setId(entityId);

        this.dao.createEntity(product, PRODUCT_TABLE_NAME, getTenant());
        product.setId(entityId);
        Integer lastIndex = this.dao.lastPosition(getTenant());
        if (lastIndex == null) {
            lastIndex = 0;
        }
        this.dao.createProduct(lastIndex + 1, product);
        this.dao.insertTranslations(entityId, product.getTranslations());
        this.dao.createOrUpdateAddons(product);

        this.dao.commit();

        return product;
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
        Integer updatedRows = this.dao.updateProduct(product);
        this.dao.createOrUpdateAddons(product);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating product");
        }
    }

    @Override
    public void delete(@Valid Product entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteAddons(entity);
        updatedRows += this.dao.deleteProductFromCollections(entity.getId());
        updatedRows += this.dao.deleteEntityEntityById(PRODUCT_TABLE_NAME, entity.getId());
        updatedRows += this.dao.detachChildren(entity.getId());
        updatedRows += this.dao.deleteEntityAndChildrenById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete product");
        }
    }

    public void moveProduct(String collectionToMove, String collectionToMoveRelativeTo,
            HasOrderedCollections.RelativePosition relativePosition) throws InvalidMoveOperation
    {
        this.dao.begin();

        List<Product> allProducts = this.findAll();
        MoveEntityInListOperation<Product> moveOp =
                new MoveEntityInListOperation<Product>(allProducts, collectionToMove,
                        collectionToMoveRelativeTo, relativePosition);

        if (moveOp.hasMoved()) {
            this.dao.updatePositions(PRODUCT_TABLE_NAME, moveOp.getEntities(), moveOp.getPositions());
        }

        this.dao.commit();
    }

    @Override
    public List<Product> findOrphanProducts()
    {
        return AddonsHelper.withAddons(this.dao.findOrphanProducts(getTenant()), this.dao);
    }

    public List<Product> findAll()
    {
        return AddonsHelper.withAddons(this.dao.findAll(PRODUCT_TABLE_NAME, PRODUCT_POSITION, getTenant()), this.dao);
    }

    public List<Product> findAll(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(
                this.dao.findAll(PRODUCT_TABLE_NAME, PRODUCT_POSITION, getTenant(), number, offset), this.dao);
    }

    @Override
    public List<Product> findByIds(List<UUID> ids)
    {
        return AddonsHelper.withAddons(this.dao.findByIds(PRODUCT_TABLE_NAME, ids), this.dao);
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(PRODUCT_TABLE_NAME, getTenant());
    }

    public Product findBySlug(String slug)
    {
        Product product = this.dao.findBySlugWithTranslations(PRODUCT_TABLE_NAME, slug, getTenant());
        if (product != null) {
            List<Addon> addons = this.dao.findAddons(product);
            product.setAddons(addons);
        }
        return product;
    }

    @Override
    public List<Product> findAllForCollection(Collection collection)
    {
        return AddonsHelper.withAddons(this.dao.findAllForCollection(collection), this.dao);
    }

    @Override
    public List<Product> findAllOnShelf(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.dao.findAllOnShelf(getTenant(), number, offset), this.dao);
    }

    @Override
    public Product findById(UUID id)
    {
        Product product = this.dao.findById(PRODUCT_TABLE_NAME, id);
        List<Addon> addons = this.dao.findAddons(product);
        product.setAddons(addons);
        return product;
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(ProductDAO.class);
        super.initialize();
    }
}
