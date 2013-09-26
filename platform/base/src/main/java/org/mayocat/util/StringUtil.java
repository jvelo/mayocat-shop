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
