package org.mayocat.rest.error;

/**
 * @version $Id$
 */
public enum StandardError implements ErrorCode
{
    NOT_A_VALID_TENANT(40401);

    private int code;

    StandardError(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public String getIdentifier()
    {
        return this.toString();
    }
}
