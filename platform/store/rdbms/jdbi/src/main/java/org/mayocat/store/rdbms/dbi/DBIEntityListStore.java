/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.mayocat.model.EntityList;
import org.mayocat.model.event.EntityCreatedEvent;
import org.mayocat.model.event.EntityCreatingEvent;
import org.mayocat.model.event.EntityUpdatedEvent;
import org.mayocat.model.event.EntityUpdatingEvent;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.EntityListStore;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import mayoapp.dao.EntityListDAO;

/**
 * DBI implementation of {@link EntityListStore}
 *
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIEntityListStore extends DBIEntityStore implements EntityListStore, Initializable
{
    public static final String ENTITY_LIST_TABLE_NAME = "entity_list";

    private EntityListDAO dao;

    @Inject
    private Logger logger;

    public List<EntityList> findListsByHint(String hint)
    {
        return this.dao.findByHint(hint, getTenant());
    }

    public EntityList create(@Valid EntityList list) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(ENTITY_LIST_TABLE_NAME, list.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        getObservationManager().notify(new EntityCreatingEvent(), list);

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        list.setId(entityId);

        this.dao.createEntity(list, ENTITY_LIST_TABLE_NAME, getTenant());
        this.dao.createEntityList(list);
        this.dao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), list);

        return list;
    }

    public void update(@Valid EntityList list) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        EntityList originalList = this.dao.findBySlug(ENTITY_LIST_TABLE_NAME, list.getSlug(), getTenant());

        if (originalList == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }

        getObservationManager().notify(new EntityUpdatingEvent(), list);

        list.setId(originalList.getId());
        Integer updatedRows = this.dao.updateEntityList(list);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating list");
        }

        getObservationManager().notify(new EntityUpdatedEvent(), list);
    }

    public void delete(@Valid EntityList entity) throws EntityDoesNotExistException
    {
        throw new RuntimeException("Not implemented");
    }

    public Integer countAll()
    {
        return this.dao.countAll(ENTITY_LIST_TABLE_NAME, getTenant());
    }

    public List<EntityList> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(ENTITY_LIST_TABLE_NAME, getTenant());
    }

    public List<EntityList> findByIds(List<UUID> ids)
    {
        return this.dao.findByIds(ENTITY_LIST_TABLE_NAME, ids);
    }

    public EntityList findById(UUID id)
    {
        return this.dao.findById(ENTITY_LIST_TABLE_NAME, id);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(EntityListDAO.class);
        super.initialize();
    }
}
