package org.mayocat.shop.billing.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.shop.billing.model.Order;

/**
 * @version $Id$
 */
public class OrderEntity implements EntityMeta
{
    public static final String ID = "order";

    public static final String PATH = "orders";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return Order.class;
    }
}
