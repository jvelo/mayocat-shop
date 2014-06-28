/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.store.jdbi;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.model.AddonGroup;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;

import mayoapp.dao.PageDAO;

import org.mayocat.addons.store.dbi.AddonsHelper;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import static org.mayocat.addons.util.AddonUtils.asMap;

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
    public Page create(@Valid Page page) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(PAGE_TABLE_NAME, page.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        page.setId(entityId);

        this.dao.createEntity(page, PAGE_TABLE_NAME, getTenant());
        Integer lastIndex = this.dao.lastPosition(getTenant());
        if (lastIndex == null) {
            lastIndex = 0;
        }
        this.dao.createPage(lastIndex + 1, page);
        this.dao.createOrUpdateAddons(page);

        this.dao.commit();

        return page;
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

        if (page.getLocalizedVersions() != null && !page.getLocalizedVersions().isEmpty()) {
            Map<Locale, Map<String, Object>> localizedVersions = page.getLocalizedVersions();
            for (Locale locale : localizedVersions.keySet()) {
                this.dao.createOrUpdateTranslation(page.getId(), locale, localizedVersions.get(locale));
            }
        }

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating page");
        }
    }

    @Override
    public void delete(@Valid Page entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteAddons(entity);
        updatedRows += this.dao.deleteEntityEntityById(PAGE_TABLE_NAME, entity.getId());
        updatedRows += this.dao.detachChildren(entity.getId());
        updatedRows += this.dao.deleteEntityAndChildrenById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete page");
        }
    }

    @Override
    public List<Page> findAll(Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.dao.findAll(PAGE_TABLE_NAME, getTenant(), number, offset), this.dao);
    }

    @Override
    public List<Page> findAllRootPages()
    {
        return AddonsHelper.withAddons(this.dao.findAllRootPages(getTenant()), this.dao);
    }

    @Override
    public List<Page> findByIds(List<UUID> ids)
    {
        return AddonsHelper.withAddons(this.dao.findByIds(PAGE_TABLE_NAME, ids), this.dao);
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(PAGE_TABLE_NAME, getTenant());
    }

    @Override
    public Page findBySlug(String slug)
    {
        Page page = this.dao.findBySlug(PAGE_TABLE_NAME, slug, getTenant());
        if (page != null) {
            List<AddonGroup> addons = this.dao.findAddons(page);
            page.setAddons(asMap(addons));
        }
        return page;
    }

    @Override
    public Page findById(UUID id)
    {
        Page page = this.dao.findById(PAGE_TABLE_NAME, id);
        List<AddonGroup> addons = this.dao.findAddons(page);
        page.setAddons(asMap(addons));
        return page;
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(PageDAO.class);
        super.initialize();
    }
}

