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
    // 400
    INSUFFICIENT_DATA(40001),
    PASSWORD_DOES_NOT_MEET_REQUIREMENTS(40002),

    // 401
    INVALID_CREDENTIALS(40101),
    ACCOUNT_REQUIRES_VALIDATION(40102),
    REQUIRES_VALID_USER(40103),

    // 403
    INSUFFICIENT_PRIVILEGES(40301),

    // 404
    NOT_A_VALID_TENANT(40401),
    USER_NOT_FOUND(40402),
    PASSWORD_RESET_KEY_NOT_FOUND(40403),
    ORDER_NOT_FOUND(40404),

    // 409
    EMAIL_ALREADY_REGISTERED(40901),
    USERNAME_ALREADY_REGISTERED(40902);

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
