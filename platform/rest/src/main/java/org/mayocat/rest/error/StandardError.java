/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.error;

/**
 * @version $Id$
 */
public enum StandardError implements ErrorCode
{
    NOT_A_VALID_TENANT(40401);

    private int code;

    StandardError(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public String getIdentifier()
    {
        return this.toString();
    }
}
