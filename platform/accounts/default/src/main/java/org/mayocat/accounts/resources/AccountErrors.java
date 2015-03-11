/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.resources;

import org.mayocat.rest.error.ErrorCode;

/**
 * REST Error codes for the accounts module
 *
 * @version $Id$
 */
public enum AccountErrors implements ErrorCode
{
    PASSWORD_NOT_STRONG_ENOUGH(30210);

    private int code;

    AccountErrors(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }

    public String getIdentifier()
    {
        return this.toString();
    }
}
