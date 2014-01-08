/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

/**
 * A type of {@link CheckoutException} that occurs in the "normal" flow of operations. Instances of this exception
 * can be treated as something that is meant to happen, without paying extra attention (as opposed to a
 * {@link CheckoutException} that is not an instance of this class).
 *
 * Examples: A user submits a second time the payment cancel form, effectively attempting to delete an order that is
 * already deleted. This is a regular checkout exception because we don't need to take additional measures besides
 * displaying an error message. This example can be opposed to the example of a payment gateway returning an error
 * linked to the global configuration of the gateway : in which case the error has to be logged and an administrator
 * informed.
 *
 * @version $Id$
 */
public class RegularCheckoutException extends CheckoutException
{
    public RegularCheckoutException()
    {
    }

    public RegularCheckoutException(String message)
    {
        super(message);
    }

    public RegularCheckoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RegularCheckoutException(Throwable cause)
    {
        super(cause);
    }
}
