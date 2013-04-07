package org.mayocat.shop.payment;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @version $Id$
 */
public interface PaymentGateway
{
    PaymentResponse purchase(BigDecimal amount, Map<String, Object> options) throws PaymentException;
}
