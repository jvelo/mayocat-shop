/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search;

public class SearchEngineException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SearchEngineException()
    {
        super();
    }

    public SearchEngineException(String message)
    {
        super(message);
    }

    public SearchEngineException(Throwable cause)
    {
        super(cause);
    }

    public SearchEngineException(String message, Throwable cause)
    {
        super(message, cause);

    }

}
