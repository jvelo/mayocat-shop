package org.mayocat.shop.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @version $Id$
 */
public interface PaymentGateway
{
    GatewayResponse purchase(BigDecimal amount, Map<Option, Object> options) throws GatewayException;

    GatewayResponse acknowledge(Map<String, List<String>> data) throws GatewayException;
}
