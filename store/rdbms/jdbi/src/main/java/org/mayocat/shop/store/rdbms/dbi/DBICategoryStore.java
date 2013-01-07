package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.rdbms.dbi.dao.CategoryDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints={"jdbi", "default"})
public class DBICategoryStore extends AbstractEntityStore implements CategoryStore, Initializable
{
    private static final String CATEGORY_TABLE_NAME = "category";

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
        this.dao.create(entityId, category);
        this.dao.insertTranslations(entityId, category.getTranslations());

        this.dao.commit();
    }

    public void update(Category entity) throws InvalidEntityException
    {
        // TODO Auto-generated method stub
    }

    public List<Category> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(CATEGORY_TABLE_NAME, getTenant(), number, offset);
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
