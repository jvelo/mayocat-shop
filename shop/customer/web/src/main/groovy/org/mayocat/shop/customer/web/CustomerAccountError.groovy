/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web

import groovy.transform.CompileStatic
import ErrorCode
import org.mayocat.rest.error.ErrorCode

/**
 * @version $Id$
 */
@CompileStatic
public enum CustomerAccountError implements ErrorCode {

    // 400
    CONNECTED_USER_DOES_NOT_MATCH(400101),
    VALIDATION_KEY_DOES_NOT_EXIST(400102),
    ACCOUNT_ALREADY_VALIDATED(400103),

    // 404
    CUSTOMER_NOT_FOUND(404101);

    private int code;

    CustomerAccountError(int code)
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