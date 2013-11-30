/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import mayoapp.dao.PaymentOperationDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component
public class DBIPaymentOperationStore implements PaymentOperationStore, Initializable
{
    @Inject
    private DBIProvider dbi;

    private PaymentOperationDAO dao;

    @Override
    public PaymentOperation create(@Valid PaymentOperation operation)
            throws EntityAlreadyExistsException, InvalidEntityException
    {
        operation.setId(UUID.randomUUID());
        this.dao.createPaymentOperation(operation);
        return operation;
    }

    @Override
    public void update(@Valid PaymentOperation entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(@Valid PaymentOperation entity) throws EntityDoesNotExistException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Integer countAll()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<PaymentOperation> findAll(Integer number, Integer offset)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<PaymentOperation> findByIds(List<UUID> ids)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public PaymentOperation findById(UUID id)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(PaymentOperationDAO.class);
    }
}
