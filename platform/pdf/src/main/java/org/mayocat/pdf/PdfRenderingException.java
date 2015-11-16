/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.pdf;

/**
 * @version $Id$
 */
public class PdfRenderingException extends Exception
{
    public PdfRenderingException() {
    }

    public PdfRenderingException(String message) {
        super(message);
    }

    public PdfRenderingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfRenderingException(Throwable cause) {
        super(cause);
    }

    public PdfRenderingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
