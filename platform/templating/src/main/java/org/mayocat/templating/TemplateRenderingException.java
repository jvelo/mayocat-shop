/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.templating;

/**
 * @version $Id$
 */
public class TemplateRenderingException extends Exception
{
    public TemplateRenderingException()
    {
    }

    public TemplateRenderingException(String message)
    {
        super(message);
    }

    public TemplateRenderingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TemplateRenderingException(Throwable cause)
    {
        super(cause);
    }

    public TemplateRenderingException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
