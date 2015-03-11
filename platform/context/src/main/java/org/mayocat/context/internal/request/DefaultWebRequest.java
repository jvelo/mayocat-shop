/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context.internal.request;

import java.net.URI;

import org.mayocat.context.request.WebRequest;
import org.mayocat.theme.Breakpoint;

import com.google.common.base.Optional;

/**
 * Default implementation of {@link WebRequest}
 *
 * @version $Id$
 */
public class DefaultWebRequest implements WebRequest
{
    private final String canonicalPath;

    private final String path;

    private final URI baseURI;

    private final boolean isApiRequest;

    private final boolean isTenantRequest;

    private final String tenantPrefix;

    private final Optional<Breakpoint> breakpoint;

    public DefaultWebRequest(URI baseURI, String canonicalPath, String path, boolean isTenantRequest,
            String tenantPrefix, boolean isApiRequest, Optional<Breakpoint> breakpoint)
    {
        this.tenantPrefix = tenantPrefix;
        this.isTenantRequest = isTenantRequest;
        this.isApiRequest = isApiRequest;
        this.baseURI = baseURI;
        this.path = path;
        this.canonicalPath = canonicalPath;
        this.breakpoint = breakpoint;
    }

    @Override
    public Optional<Breakpoint> getBreakpoint()
    {
        return this.breakpoint;
    }

    @Override
    public String getCanonicalPath()
    {
        return canonicalPath;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public boolean isApiRequest()
    {
        return isApiRequest;
    }

    @Override
    public boolean isTenantRequest()
    {
        return isTenantRequest;
    }

    @Override
    public URI getBaseUri()
    {
        return baseURI;
    }

    @Override
    public String getTenantPrefix()
    {
        return tenantPrefix;
    }
}
