package org.mayocat.theme;

import org.xwiki.component.annotation.Role;

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
    Breakpoint getBreakpoint(String userAgent);
}
