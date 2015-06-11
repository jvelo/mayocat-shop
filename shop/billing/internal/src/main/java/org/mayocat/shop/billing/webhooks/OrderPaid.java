package org.mayocat.shop.billing.webhooks;

import org.mayocat.webhooks.Hook;
import org.mayocat.webhooks.Webhook;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(OrderPaid.ID)
public class OrderPaid implements Webhook
{
    static final String ID = "order_paid";

    @Override
    public String getName() {
        return ID;
    }
}
