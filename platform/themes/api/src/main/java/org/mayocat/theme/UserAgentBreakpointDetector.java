/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * Detects a {@link Breakpoint} from a User agent string
 *
 * @version $Id$
 */
@Role
public interface UserAgentBreakpointDetector
{
    /**
     * @param userAgent the UA to get the breakpoint for
     * @return
     */
    Optional<Breakpoint> getBreakpoint(String userAgent);
}
