/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import mayoapp.dao.OrderDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIOrderStore extends DBIEntityStore implements OrderStore, Initializable
{
    private static final String ORDER_TABLE_NAME = "purchase_order";

    private OrderDAO dao;

    @Override
    public Order create(@Valid Order order) throws EntityAlreadyExistsException, InvalidEntityException
    {
        this.dao.begin();

        String slug = String.format("%08d", lastOrderNumber() + 1);
        order.setSlug(slug);

        order.setId(UUID.randomUUID());

        this.dao.createEntity(order, ORDER_TABLE_NAME, getTenant());
        this.dao.createOrder(order);

        this.dao.commit();

        return order;
    }

    private Integer lastOrderNumber()
    {
        return this.dao.lastOrderNumber(getTenant());
    }


    @Override
    public void update(@Valid Order order) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.dao.begin();

        Order originalOrder = this.findBySlug(order.getSlug());
        if (originalOrder == null) {
            this.dao.commit();
            throw new EntityDoesNotExistException();
        }
        order.setId(originalOrder.getId());
        Integer updatedRows = this.dao.updateOrder(order);

        this.dao.commit();

        if (updatedRows <= 0) {
            throw new StoreException("No rows was updated when updating order");
        }
    }

    @Override
    public void delete(@Valid Order entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteEntityEntityById(ORDER_TABLE_NAME, entity.getId());
        updatedRows += this.dao.detachChildren(entity.getId());
        updatedRows += this.dao.deleteEntityAndChildrenById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete order");
        }
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(ORDER_TABLE_NAME, getTenant());
    }

    @Override
    public List<Order> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(ORDER_TABLE_NAME, getTenant(), number, offset);
    }

    @Override
    public List<Order> findByIds(List<UUID> ids)
    {
        return this.dao.findByIds(ORDER_TABLE_NAME, ids);
    }

    @Override
    public Order findById(UUID id)
    {
        return this.dao.findByIdWithCustomer(id);
    }

    @Override
    public List<Order> findAllPaidOrAwaitingPayment(Integer number, Integer offset)
    {
        return this.dao.findAllPaidOrAwaitingPayment(number, offset, getTenant());
    }

    @Override
    public Order findBySlug(String slug)
    {
        return this.dao.findBySlugWithCustomer(slug, getTenant());
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = getDbi().onDemand(OrderDAO.class);
        super.initialize();
    }
}
