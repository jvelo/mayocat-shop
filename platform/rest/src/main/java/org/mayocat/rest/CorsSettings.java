package org.mayocat.rest;

/**
 * CORS settings. See {@link org.mayocat.rest.jersey.CorsResponseFilter}
 *
 * @version $Id$
 */
public class CorsSettings
{
    private Boolean enabled = false;

    public Boolean isEnabled()
    {
        return enabled;
    }
}
