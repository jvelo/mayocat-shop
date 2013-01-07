package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.rdbms.dbi.dao.ProductDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints={"jdbi", "default"})
public class DBIProductStore extends AbstractEntityStore implements ProductStore, Initializable
{
    @Inject
    private DBIProvider dbi;
    
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
    public void update(Product entity) throws InvalidEntityException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Product> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(PRODUCT_TABLE_NAME, getTenant(), number, offset);
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
        this.dao = this.dbi.get().onDemand(ProductDAO.class);
    }

    public Product findBySlug(String slug)
    {
        return this.dao.findBySlugWithTranslations(PRODUCT_TABLE_NAME, slug, getTenant());
    }

    @Override
    public List<Product> findAllInCategory(Category category, int number, int offset)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
