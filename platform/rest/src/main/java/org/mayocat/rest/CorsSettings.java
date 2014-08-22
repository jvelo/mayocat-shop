/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest;

/**
 * CORS settings. See {@link org.mayocat.rest.jersey.CorsResponseFilter}
 *
 * @version $Id$
 */
public class CorsSettings
{
    private Boolean enabled = false;

    private String allowOrigin = "*";

    private String allowMethods = "GET, POST, OPTIONS, HEAD, PUT, DELETE";

    private String allowHeaders = "Accept, Content-Type, Content-Range, Content-Disposition, Content-Description, X-Requested-With";

    private String exposeHeaders = "Location, Content-Length";

    private Boolean allowCredentials = false;

    private Boolean copyRequestedHeaders = false;

    public Boolean isEnabled()
    {
        return enabled;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public String getAllowOrigin()
    {
        return allowOrigin;
    }

    public String getAllowMethods()
    {
        return allowMethods;
    }

    public Boolean isAllowCredentials()
    {
        return allowCredentials;
    }

    public String getAllowHeaders()
    {
        return allowHeaders;
    }

    public String getExposeHeaders()
    {
        return exposeHeaders;
    }

    public Boolean isCopyRequestedHeaders()
    {
        return copyRequestedHeaders;
    }
}
