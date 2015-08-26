/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * @version $Id$
 */
public class PaymentRequest
{
    private final PaymentStatus status;

    private final RequiredAction nextAction;

    private final ImmutableMap<String, Object> data;

    private final Optional<String> redirectionTarget;

    public PaymentRequest(PaymentStatus status, RequiredAction nextAction) {
        this(status, nextAction, ImmutableMap.<String, Object>of(), Optional.<String>absent());
    }

    public PaymentRequest(PaymentStatus status, RequiredAction nextAction, ImmutableMap<String, Object> data) {
        this(status, nextAction, data, Optional.<String>absent());
    }

    public PaymentRequest(PaymentStatus status, RequiredAction nextAction, ImmutableMap<String, Object> data, Optional<String> redirectionTarget) {
        this.redirectionTarget = redirectionTarget;
        this.data = data;
        this.nextAction = nextAction;
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public RequiredAction getNextAction() {
        return nextAction;
    }

    public ImmutableMap<String, Object> getData() {
        return data;
    }

    public Optional<String> getRedirectionTarget() {
        return redirectionTarget;
    }
}
