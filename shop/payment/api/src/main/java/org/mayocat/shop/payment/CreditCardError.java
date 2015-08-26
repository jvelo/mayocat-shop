/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

/**
 * @version $Id$
 */
public enum CreditCardError
{
    INVALID_NUMBER,
    INVALID_EXPIRATION_DATE,
    INVALID_VERIFICATION_CODE,
    CARD_EXPIRED,
    CARD_DECLINED,
    OTHER
}
