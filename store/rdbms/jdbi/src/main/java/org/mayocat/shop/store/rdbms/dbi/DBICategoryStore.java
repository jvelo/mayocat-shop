package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.InvalidMoveOperation;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.rdbms.dbi.dao.CategoryDAO;
import org.mayocat.shop.store.rdbms.dbi.dao.util.CollectionUtil;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.collect.Lists;

@Component(hints={"jdbi", "default"})
public class DBICategoryStore extends AbstractEntityStore implements CategoryStore, Initializable
{
    private static final String CATEGORY_TABLE_NAME = "category";

    public static final String CATEGORY_POSITION = "category.position";

    @Inject
    private DBIProvider dbi;

    private CategoryDAO dao;

    public void create(Category category) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(category.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        Long entityId = this.dao.createEntity(category, CATEGORY_TABLE_NAME, getTenant());
        Integer lastIndex = this.dao.lastPosition(getTenant());
        this.dao.create(entityId, lastIndex == null ? 0 : ++lastIndex, category);
        this.dao.insertTranslations(entityId, category.getTranslations());

        this.dao.commit();
    }

    public void update(Category category) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        Category originalCategory = this.dao.findBySlug(category.getSlug(), getTenant());
        if (originalCategory == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }
        category.setId(originalCategory.getId());
        Integer updatedRows = this.dao.update(category);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating category");
        }
    }

    public void moveCategory(String categoryToMove, String categoryToMoveRelativeTo,
            RelativePosition relativePosition) throws InvalidMoveOperation
    {
        this.dao.begin();

        List<Category> allCategories = this.findAll();
        MoveEntityInListOperation<Category> moveOp =
                new MoveEntityInListOperation<Category>(allCategories, categoryToMove,
                        categoryToMoveRelativeTo, relativePosition);

        if (moveOp.hasMoved()) {
            this.dao.updatePositions(CATEGORY_TABLE_NAME, moveOp.getEntities(), moveOp.getPositions());
        }

        this.dao.commit();
    }

    public List<Category> findAllForProduct(Product product)
    {
        return this.dao.findAllForProduct(product);
    }

    public List<Category> findAll()
    {
        return this.dao.findAll(CATEGORY_TABLE_NAME, CATEGORY_POSITION, getTenant());
    }

    public List<Category> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(CATEGORY_TABLE_NAME, CATEGORY_POSITION, getTenant(), number, offset);
    }

    public Category findById(Long id)
    {
        return this.dao.findById(CATEGORY_TABLE_NAME, id);
    }

    public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(CategoryDAO.class);
    }

    public Category findBySlug(String slug)
    {
        return this.dao.findBySlugWithTranslations(CATEGORY_TABLE_NAME, slug, getTenant());
    }
}
