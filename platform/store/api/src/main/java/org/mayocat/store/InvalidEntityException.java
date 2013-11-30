/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store;

import java.util.Collections;

import com.google.common.collect.ImmutableList;

public class InvalidEntityException extends Exception
{
    /**
     * Generated serial UID. Change when the serialization form of this exception changes.
     */
    private static final long serialVersionUID = 645796421122785119L;
    
    private final ImmutableList<String> errors;

    public InvalidEntityException(String message)
    {
        super(message);
        errors = ImmutableList.of();
    }

    public InvalidEntityException(String message, Iterable<String> errors)
    {
       super(message);
        this.errors = ImmutableList.copyOf(errors);
    }

    public ImmutableList<String> getErrors() {
        return errors;
    }
}
