/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.home.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.cms.home.model.HomePage;
import org.mayocat.cms.home.store.HomePageStore;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.event.EntityCreatedEvent;
import org.mayocat.model.event.EntityCreatingEvent;
import org.mayocat.model.event.EntityUpdatedEvent;
import org.mayocat.model.event.EntityUpdatingEvent;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import mayoapp.dao.HomePageDAO;

import static org.mayocat.addons.util.AddonUtils.asMap;

/**
 * @version $Id$
 */
@Component
public class DBIHomePageStore extends DBIEntityStore implements HomePageStore, Initializable
{
    private HomePageDAO dao;

    private static final String HOME_PAGE_TYPE = "home";

    public HomePage create(@Valid HomePage homePage) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(HOME_PAGE_TYPE, homePage.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        homePage.setId(entityId);

        this.dao.createEntity(homePage, HOME_PAGE_TYPE, getTenant());
        this.dao.createOrUpdateAddons(homePage);

        this.dao.commit();

        return homePage;
    }

    public HomePage getOrCreate(HomePage homePage) throws InvalidEntityException
    {
        HomePage original;

        this.dao.begin();

        original = this.find();

        if (original != null) {
            this.dao.commit();
            return original;
        }

        getObservationManager().notify(new EntityCreatingEvent(), homePage);

        UUID entityId = UUID.randomUUID();
        homePage.setId(entityId);

        this.dao.createEntityIfItDoesNotExist(homePage, HOME_PAGE_TYPE, getTenant());
        this.dao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), homePage);

        return this.find();
    }

    public void update(@Valid HomePage entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        HomePage homePage = find();

        if (homePage == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }

        getObservationManager().notify(new EntityUpdatingEvent(), entity);

        entity.setId(homePage.getId());

        this.dao.createOrUpdateAddons(entity);
        this.dao.commit();

        getObservationManager().notify(new EntityUpdatedEvent(), entity);
    }

    public void delete(@Valid HomePage entity) throws EntityDoesNotExistException
    {
        throw new RuntimeException("Not implemented");
    }

    public Integer countAll()
    {
        throw new RuntimeException("Not implemented");
    }

    public List<HomePage> findAll(Integer number, Integer offset)
    {
        throw new RuntimeException("Not implemented");
    }

    public List<HomePage> findByIds(List<UUID> ids)
    {
        throw new RuntimeException("Not implemented");
    }

    public HomePage findById(UUID id)
    {
        throw new RuntimeException("Not implemented");
    }

    private HomePage find()
    {
        HomePage page = this.dao.find(getTenant());
        if (page != null) {
            List<AddonGroup> addons = this.dao.findAddons(page);
            page.setAddons(asMap(addons));
        }
        return page;
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(HomePageDAO.class);
        super.initialize();
    }
}
