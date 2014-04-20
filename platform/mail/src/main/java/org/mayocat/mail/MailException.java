/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail;

/**
 * Exception related to the sending of emails
 *
 * @version $Id$
 */
public class MailException extends Exception
{
    public MailException()
    {
        super();
    }

    public MailException(String message)
    {
        super(message);
    }

    public MailException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MailException(Throwable cause)
    {
        super(cause);
    }
}
