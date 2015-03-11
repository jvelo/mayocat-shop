/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.shop.payment.model.PaymentOperation

/**
 * @version $Id$
 */
@CompileStatic
class PaymentOperationApiObject
{
    UUID id;

    String gatewayId;

    String externalId;

    String result

    Map<String, Object> memo;

    PaymentOperationApiObject withPaymentOperation(PaymentOperation operation)
    {
        id = operation.id
        gatewayId = operation.gatewayId
        externalId = operation.externalId
        result = operation.result.toString().toLowerCase()
        memo = memo

        this
    }
}
