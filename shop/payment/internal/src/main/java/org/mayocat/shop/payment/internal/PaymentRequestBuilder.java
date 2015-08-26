/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.internal;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import org.mayocat.shop.payment.PaymentRequest;
import org.mayocat.shop.payment.PaymentStatus;
import org.mayocat.shop.payment.RequiredAction;

/**
 * @version $Id$
 */
public class PaymentRequestBuilder
{
    private PaymentStatus status = PaymentStatus.NONE;

    private RequiredAction nextAction = RequiredAction.NONE;

    private ImmutableMap<String, Object> data = ImmutableMap.of();

    private Optional<String> redirectionTarget = Optional.absent();

    public PaymentRequestBuilder withStatus(PaymentStatus status) {
        this.status = status;
        return this;
    }

    public PaymentRequestBuilder withNextAction(RequiredAction nextAction) {
        this.nextAction = nextAction;
        return this;
    }

    public PaymentRequestBuilder withData(Map<String, Object> data) {
        this.data = ImmutableMap.copyOf(data);
        return this;
    }

    public PaymentRequestBuilder withRedirectionTarget(String target) {
        this.redirectionTarget = Optional.fromNullable(target);
        return this;
    }

    public PaymentRequest build() {
        return new PaymentRequest(status, nextAction, data, redirectionTarget);
    }
}
