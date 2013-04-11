package org.mayocat.shop.payment;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @version $Id$
 */
public interface PaymentGateway
{
    boolean isExternal();

    PaymentResponse purchase(BigDecimal amount, Map<Option, Object> options) throws PaymentException;
}
