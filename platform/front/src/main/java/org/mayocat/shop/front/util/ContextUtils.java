/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
