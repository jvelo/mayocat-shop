/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

/**
 * Exception thrown when trying to store invalid gateway customer data
 *
 * @version $Id$
 */
public class InvalidGatewayDataException extends Exception
{
    public InvalidGatewayDataException()
    {
    }

    public InvalidGatewayDataException(String message)
    {
        super(message);
    }
}
