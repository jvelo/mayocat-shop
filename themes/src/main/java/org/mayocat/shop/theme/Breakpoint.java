package org.mayocat.shop.theme;

/**
 * This helps providing the SS in RESS by making it possible to return different templates for different "breakpoints"
 * such as mobile, tablet, etc.
 *
 * See RESS: Responsive Design + Server Side Components http://www.lukew.com/ff/entry.asp?1392
 *
 * @see {@link UserAgentBreakpointDetector}
 * @version $Id$
 */
public enum Breakpoint
{
    DEFAULT (""),
    MOBILE ("mobile");

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