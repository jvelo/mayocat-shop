package org.mayocat.accounts.resources;

import org.mayocat.rest.error.ErrorCode;

/**
 * REST Error codes for the accounts module
 *
 * @version $Id$
 */
public enum AccountErrors implements ErrorCode
{
    PASSWORD_NOT_STRONG_ENOUGH(30210);

    private int code;

    AccountErrors(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }

    public String getIdentifier()
    {
        return this.toString();
    }
}
