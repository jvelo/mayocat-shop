/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

/**
 * This helps providing the SS in RESS by making it possible to return different templates for different "breakpoints"
 * such as mobile, tablet, etc.
 *
 * See RESS: Responsive Design + Server Side Components http://www.lukew.com/ff/entry.asp?1392
 *
 * @version $Id$
 * @see {@link UserAgentBreakpointDetector}
 */
public enum Breakpoint
{
    MOBILE("mobile"),
    TABLET("tablet"),
    TV("tv");

    private String folder;

    Breakpoint(String folder)
    {
        this.folder = folder;
    }

    public String getFolder()
    {
        return folder;
    }
}