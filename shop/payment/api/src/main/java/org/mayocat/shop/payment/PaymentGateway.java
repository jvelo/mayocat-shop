package org.mayocat.shop.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @version $Id$
 */
public interface PaymentGateway
{
    boolean isExternal();

    PaymentResponse purchase(BigDecimal amount, Map<Option, Object> options) throws PaymentException;

    PaymentResponse acknowledge(Map<String, List<String>> data) throws PaymentException;

}
