/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

/**
 * @version $Id$
 */
public class CheckoutException extends Exception
{
    public CheckoutException()
    {
    }

    public CheckoutException(String message)
    {
        super(message);
    }

    public CheckoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CheckoutException(Throwable cause)
    {
        super(cause);
    }
}
