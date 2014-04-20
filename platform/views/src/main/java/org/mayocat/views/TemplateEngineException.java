/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.views;

/**
 * @version $Id$
 */
public class TemplateEngineException extends Exception
{
    public TemplateEngineException()
    {
        super();
    }

    public TemplateEngineException(Throwable t)
    {
        super(t);
    }

    public TemplateEngineException(String message, Throwable t)
    {
        super(message, t);
    }
}
