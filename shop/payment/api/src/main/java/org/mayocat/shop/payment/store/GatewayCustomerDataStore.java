package org.mayocat.shop.payment.store;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.payment.model.GatewayCustomerData;

import com.google.common.base.Optional;

/**
 * Store interface for accessing {@link GatewayCustomerData}.
 *
 * @version $Id$
 */
public interface GatewayCustomerDataStore
{
    Optional<GatewayCustomerData> getCustomerData(Customer customer, String gatewayId);
}
