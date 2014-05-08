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

    public EntityList findListByHintAndParentId(String hint, UUID parentId)
    {
        return this.dao.findByHintAndParentId(hint, parentId);
    }

    public EntityList create(@Valid EntityList list) throws EntityAlreadyExistsException, InvalidEntityException
    {
        EntityList originalList;

        this.dao.begin();

        originalList = getStoredEntityList(list);

        if (originalList != null) {
            this.dao.commit();
            throw new EntityAlreadyExistsException();
        }

        getObservationManager().notify(new EntityCreatingEvent(), list);

        UUID entityId = UUID.randomUUID();
        list.setId(entityId);

        if (list.getParentId() != null) {
            this.dao.createChildEntity(list, ENTITY_LIST_TABLE_NAME, getTenant());
        } else {
            this.dao.createEntity(list, ENTITY_LIST_TABLE_NAME, getTenant());
        }
        this.dao.createEntityList(list);
        this.dao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), list);

        return list;
    }

    public EntityList getOrCreate(EntityList list) throws InvalidEntityException
    {
        EntityList originalList;

        this.dao.begin();

        originalList = getStoredEntityList(list);

        if (originalList != null) {
            this.dao.commit();
            return originalList;
        }

        getObservationManager().notify(new EntityCreatingEvent(), list);

        UUID entityId = UUID.randomUUID();
        list.setId(entityId);

        Integer created;

        if (list.getParentId() != null) {
            created = this.dao.createChildEntityIfItDoesNotExist(list, ENTITY_LIST_TABLE_NAME, getTenant());
        } else {
            created = this.dao.createEntityIfItDoesNotExist(list, ENTITY_LIST_TABLE_NAME, getTenant());
        }
        if (created > 0) {
            this.dao.createEntityList(list);
        }

        this.dao.commit();

        getObservationManager().notify(new EntityCreatedEvent(), list);

        return getStoredEntityList(list);
    }

    public void update(@Valid EntityList list) throws EntityDoesNotExistException, InvalidEntityException
    {
        EntityList originalList;

        this.dao.begin();

        originalList = getStoredEntityList(list);

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

    public void addEntityToList(EntityList list, UUID entity) throws EntityDoesNotExistException
    {
        EntityList originalList;

        originalList = getStoredEntityList(list);

        if (originalList == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }

        getObservationManager().notify(new EntityUpdatingEvent(), originalList);

        this.dao.addEntityToList(list.getId(), entity);

        getObservationManager().notify(new EntityUpdatedEvent(), originalList);
    }

    public void removeEntityFromList(EntityList list, UUID entity) throws EntityDoesNotExistException
    {
        EntityList originalList;

        originalList = getStoredEntityList(list);

        if (originalList == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }

        getObservationManager().notify(new EntityUpdatingEvent(), originalList);

        this.dao.removeEntityFromList(list.getId(), entity);

        getObservationManager().notify(new EntityUpdatedEvent(), originalList);
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

    private EntityList getStoredEntityList(EntityList list)
    {
        if (list.getParentId() != null) {
            return this.dao.findBySlug(ENTITY_LIST_TABLE_NAME, list.getSlug(), getTenant(), list.getParentId());
        } else {
            return this.dao.findBySlug(ENTITY_LIST_TABLE_NAME, list.getSlug(), getTenant());
        }
    }
}
