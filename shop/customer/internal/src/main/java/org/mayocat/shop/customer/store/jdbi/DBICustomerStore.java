/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.shop.customer.store.CustomerStore;
import org.mayocat.shop.customer.model.Customer;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import mayoapp.dao.CustomerDAO;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBICustomerStore extends DBIEntityStore implements CustomerStore, Initializable
{
    private static final String CUSTOMER_TABLE_NAME = "customer";

    private CustomerDAO dao;

    @Override
    public Customer create(@Valid Customer customer) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.exists(customer.getSlug())) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        UUID entityId = UUID.randomUUID();
        customer.setId(entityId);

        if (getTenant() == null) {
            this.dao.createEntity(customer, CUSTOMER_TABLE_NAME);
        } else {
            this.dao.createEntity(customer, CUSTOMER_TABLE_NAME, getTenant());
        }
        this.dao.create(customer);
        this.dao.commit();

        return customer;
    }

    @Override
    public void update(@Valid Customer customer) throws EntityDoesNotExistException, InvalidEntityException
    {
        // TODO handle global customers

        if (this.dao.findBySlug(CUSTOMER_TABLE_NAME, customer.getSlug(), getTenant()) == null) {
            throw new EntityDoesNotExistException();
        }
        this.dao.updateCustomer(customer);
    }

    @Override
    public void delete(@Valid Customer entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteEntityEntityById(CUSTOMER_TABLE_NAME, entity.getId());
        updatedRows += this.dao.detachChildren(entity.getId());
        updatedRows += this.dao.deleteEntityAndChildrenById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete customer");
        }
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(CUSTOMER_TABLE_NAME, getTenant());
    }

    @Override
    public List<Customer> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(CUSTOMER_TABLE_NAME, getTenant(), number, offset);
    }

    @Override
    public List<Customer> findByIds(List<UUID> ids)
    {
        return this.dao.findByIds(CUSTOMER_TABLE_NAME, ids);
    }

    @Override
    public Customer findById(UUID id)
    {
        return this.dao.findById(CUSTOMER_TABLE_NAME, id);
    }

    @Override
    public Customer findBySlug(String slug)
    {
        return this.dao.findBySlug(CUSTOMER_TABLE_NAME, slug, getTenant());
    }

    @Override
    public Customer findByUserId(UUID userId)
    {
        return this.dao.findByUserId(userId);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = getDbi().onDemand(CustomerDAO.class);
        super.initialize();
    }

    private boolean exists(String slug)
    {
        if (getTenant() == null) {
            return this.dao.findBySlug(CUSTOMER_TABLE_NAME, slug) != null;
        } else {
            return this.dao.findBySlug(CUSTOMER_TABLE_NAME, slug, getTenant()) != null;
        }
    }
}
