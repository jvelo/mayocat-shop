/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role
{

    GOD     ("god"),     /* Marketplace admin */
    ADMIN   ("admin"),   /* Shop admin */
    NONE    ("none")     /* No role */
    
    ;
    
    private String code;
    
    private Role(final String code)
    {
        this.code = code;
    };

    @JsonCreator
    public static Role fromJson(String text)
    {
        return valueOf(text.toUpperCase());
    }

    @Override
    public String toString() {
        return code;
    }
    
}
