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
public class GatewayException extends Exception
{
    public GatewayException()
    {
        super();
    }

    public GatewayException(String message)
    {
        super(message);
    }

    public GatewayException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GatewayException(Throwable t)
    {
        super(t);
    }
}
