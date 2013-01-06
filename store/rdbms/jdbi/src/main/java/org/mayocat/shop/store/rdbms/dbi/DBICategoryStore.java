package org.mayocat.shop.store.rdbms.dbi;

import javax.inject.Inject;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.rdbms.dbi.dao.CategoryDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component(hints={"jdbi", "default"})
public class DBICategoryStore implements CategoryStore, Initializable
{

    private static final String CATEGORY_TABLE_NAME = "category";

    @Inject
    private DBIProvider dbi;

    private CategoryDAO dao;

    public void create(Category category, Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException, StoreException
    {
        if (this.dao.findBySlug(category.getSlug(), tenant) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();
                
        Long entityId = this.dao.createEntity(category, CATEGORY_TABLE_NAME, tenant);
        this.dao.create(entityId, category);
        this.dao.insertTranslations(entityId, category.getTranslations());
        
        this.dao.commit();
    }

    @Override
    public void update(Category entity) throws InvalidEntityException, StoreException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Category findById(Long id) throws StoreException
    {
        return this.dao.findById(CATEGORY_TABLE_NAME, id);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(CategoryDAO.class);
    }

    @Override
    public Category findBySlug(String slug, Tenant tenant)
    {
        return this.dao.findBySlugWithTranslations(CATEGORY_TABLE_NAME, slug, tenant);
    }
    
    @Override
    public void create(Category category) throws EntityAlreadyExistsException, InvalidEntityException, StoreException
    {
        // FIXME KIIIIIIIIILL MEEEEEEEE
        Tenant t = new Tenant(new Long(1));
        t.setSlug("shop");
        this.create(category, t);
    }

}
