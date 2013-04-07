package org.mayocat.shop.payment;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface GatewayFactory<PG extends PaymentGateway>
{
    PG createGateway();
}
