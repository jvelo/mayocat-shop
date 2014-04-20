/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gateway to execute payment-related operations, like authorizing or capturing funds transfers. A gateway represents an
 * electronic payment terminal, typically associated with a seller account (that will receive the funds when funds
 * transfers are successfully executed against the gateway).
 *
 * @version $Id$
 */
public interface PaymentGateway
{
    /**
     * Authorize and capture a certain amount of money against the gateway.
     *
     * @param amount the amount of money to capture
     * @param data options associated with this transfer (like the currency, the return to URL, the order ID, etc.)
     * @return a response as a {@link GatewayResponse}
     * @throws GatewayException when an unexpected error occurs
     */
    GatewayResponse purchase(BigDecimal amount, Map<PaymentData, Object> data) throws GatewayException;

    /**
     * Acknowledge an information from a third party server. This is typically used by gateway that have an asynchronous
     * status acknowledgement mechanism where the status of an operation (fund transfer) is POST-ed back to an URL (of
     * ours) by the third party server.
     *
     * @param data the POST data associated with this acknowledgement request.
     * @throws GatewayException when an un-expected exception occurs.
     */
    GatewayResponse acknowledge(UUID orderId, Map<String, List<String>> data) throws GatewayException;
}
