package org.mayocat.shop.front.util;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.base.Strings;

/**
 * @version $Id$
 */
public class BindingUtils
{
    public static String safeString(String string)
    {
        return StringEscapeUtils.escapeHtml4(Strings.nullToEmpty(string));
    }

    public static String safeHtml(String string)
    {
        return Strings.nullToEmpty(string);
    }
}
