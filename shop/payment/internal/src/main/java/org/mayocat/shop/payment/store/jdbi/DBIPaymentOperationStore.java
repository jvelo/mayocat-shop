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
import org.mayocat.store.rdbms.dbi.dao.PaymentOperationDAO;
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
    public UUID create(@Valid PaymentOperation operation)
            throws EntityAlreadyExistsException, InvalidEntityException
    {
        return this.dao.createPaymentOperation(operation);
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
