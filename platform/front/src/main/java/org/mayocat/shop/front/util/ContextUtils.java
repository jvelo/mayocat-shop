package org.mayocat.shop.front.util;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.base.Strings;

/**
 * @version $Id$
 */
public class ContextUtils
{
    public static String safeString(String string)
    {
        return string == null ? null : StringEscapeUtils.escapeHtml4(string);
    }

    public static String safeHtml(String string)
    {
        return string;
    }
}
