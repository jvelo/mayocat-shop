package org.mayocat.cms.pages.store.jdbi;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.model.Addon;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.mayocat.store.rdbms.dbi.dao.PageDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIPageStore extends DBIEntityStore implements PageStore, Initializable
{
    private static final String PAGE_POSITION = "page.position";

    private static final String PAGE_TABLE_NAME = "page";

    private PageDAO dao;

    @Override
    public void create(@Valid Page page) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(PAGE_TABLE_NAME, page.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        Long entityId = this.dao.createEntity(page, PAGE_TABLE_NAME, getTenant());
        page.setId(entityId);
        Integer lastIndex = this.dao.lastPosition(getTenant());
        if (lastIndex == null) {
            lastIndex = 0;
        }
        this.dao.createPage(entityId, lastIndex + 1, page);
        this.dao.insertTranslations(entityId, page.getTranslations());
        this.dao.createOrUpdateAddons(page);

        this.dao.commit();
    }

    @Override
    public void update(@Valid Page page) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        Page originalPage = this.findBySlug(page.getSlug());
        if (originalPage == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }
        page.setId(originalPage.getId());
        Integer updatedRows = this.dao.updatePage(page);
        this.dao.createOrUpdateAddons(page);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating page");
        }
    }

    @Override
    public List<Page> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(PAGE_TABLE_NAME, getTenant(), number, offset);
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(PAGE_TABLE_NAME, getTenant());
    }

    @Override
    public Page findBySlug(String slug)
    {
        Page page = this.dao.findBySlugWithTranslations(PAGE_TABLE_NAME, slug, getTenant());
        List<Addon> addons = this.dao.findAddons(page);
        page.setAddons(addons);
        return page;
    }

    @Override
    public Page findById(Long id)
    {
        Page page = this.dao.findById(PAGE_TABLE_NAME, id);
        List<Addon> addons = this.dao.findAddons(page);
        page.setAddons(addons);
        return page;
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(PageDAO.class);
        super.initialize();
    }
}

