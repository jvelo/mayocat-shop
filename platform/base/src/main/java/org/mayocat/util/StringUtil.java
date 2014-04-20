/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.util;

/**
 * @version $Id$
 */
public class StringUtil
{
    /**
     * Transform a camelCase notation to a snake_case notation.
     *
     * @param string the string to snakify
     * @return the snakified string
     */
    public static String snakify(String string)
    {
        String result = "";
        boolean didLowerCase = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (Character.isUpperCase(c)) {
                if (!didLowerCase && result.length() > 0 && result.charAt(result.length() - 1) != '_') {
                    result += '_';
                }
                c = Character.toLowerCase(c);
                didLowerCase = true;
            } else {
                didLowerCase = false;
            }
            result += c;
        }
        return result;
    }
}
