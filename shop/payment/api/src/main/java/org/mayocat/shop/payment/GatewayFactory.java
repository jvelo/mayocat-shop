package org.mayocat.shop.payment;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface GatewayFactory
{
    String getId();

    PaymentGateway createGateway();
}
