package org.mayocat.shop.billing.store.jdbi;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.rdbms.dbi.DBIEntityStore;
import org.mayocat.store.rdbms.dbi.dao.OrderDAO;
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
    public Long create(@Valid Order order) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(ORDER_TABLE_NAME, order.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        this.dao.createEntity(order, ORDER_TABLE_NAME, getTenant());
        Long entityId = this.dao.getId(order, ORDER_TABLE_NAME, getTenant());
        this.dao.createOrder(entityId, order);

        this.dao.commit();

        return entityId;
    }

    @Override
    public void update(@Valid Order entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        throw new UnsupportedOperationException("Not implemented");
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
    public List<Order> findByIds(List<Long> ids)
    {
        return this.dao.findByIds(ORDER_TABLE_NAME, ids);
    }

    @Override
    public Order findById(Long id)
    {
        return this.dao.findById(ORDER_TABLE_NAME, id);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = getDbi().onDemand(OrderDAO.class);
        super.initialize();
    }
}
