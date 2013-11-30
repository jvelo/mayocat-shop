/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.security;

public class EncryptionException extends Exception
{

    /**
     * Generated serial version. Change when the serialization of this class changes.
     */
    private static final long serialVersionUID = -7902103228849842206L;

    public EncryptionException()
    {
        super();
    }
    
    public EncryptionException(Throwable t)
    {
        super(t);
    }
    
    public EncryptionException(String message)
    {
        super(message);
    }

    public EncryptionException(String message, Throwable t)
    {
        super(message, t);
    }
}
