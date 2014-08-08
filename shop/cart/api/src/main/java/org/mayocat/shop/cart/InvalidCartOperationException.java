/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart;

/**
 * @version $Id$
 */
public class InvalidCartOperationException extends Exception
{
    public InvalidCartOperationException()
    {
    }

    public InvalidCartOperationException(String message)
    {
        super(message);
    }

    public InvalidCartOperationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidCartOperationException(Throwable cause)
    {
        super(cause);
    }

    public InvalidCartOperationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
