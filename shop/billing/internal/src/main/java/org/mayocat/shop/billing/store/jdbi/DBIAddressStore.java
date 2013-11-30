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

import javax.inject.Inject;
import javax.validation.Valid;

import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.store.AddressStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import mayoapp.dao.AddressDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIAddressStore implements AddressStore, Initializable
{
    @Inject
    private DBIProvider dbi;

    private AddressDAO dao;

    @Override
    public Address create(@Valid Address address) throws EntityAlreadyExistsException, InvalidEntityException
    {
        UUID id = UUID.randomUUID();
        address.setId(id);
        this.dao.createAddress(address);
        return address;
    }

    @Override
    public void update(@Valid Address entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(@Valid Address entity) throws EntityDoesNotExistException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Integer countAll()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Address> findAll(Integer number, Integer offset)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Address> findByIds(List<UUID> ids)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Address findById(UUID id)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(AddressDAO.class);
    }
}
