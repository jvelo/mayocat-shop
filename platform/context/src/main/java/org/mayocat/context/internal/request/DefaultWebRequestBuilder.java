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
import com.google.common.base.Preconditions;

/**
 * @version $Id$
 */
public class DefaultWebRequestBuilder
{
    private String canonicalPath;

    private String path;

    private URI baseURI;

    private boolean isApiRequest = false;

    private boolean isTenantRequest = false;

    private String tenantPrefix = "";

    private Optional<Breakpoint> breakpoint = Optional.absent();

    public DefaultWebRequestBuilder canonicalPath(String path)
    {
        this.canonicalPath = path;
        return this;
    }

    public DefaultWebRequestBuilder path(String path)
    {
        this.path = path;
        return this;
    }

    public DefaultWebRequestBuilder baseURI(URI baseURI)
    {
        this.baseURI = baseURI;
        return this;
    }

    public DefaultWebRequestBuilder apiRequest(boolean isApiRequest)
    {
        this.isApiRequest = isApiRequest;
        return this;
    }

    public DefaultWebRequestBuilder tenantRequest(boolean isTenantRequest)
    {
        this.isTenantRequest = isTenantRequest;
        return this;
    }

    public DefaultWebRequestBuilder tenantPrefix(String tenantPrefix)
    {
        this.tenantPrefix = tenantPrefix;
        return this;
    }

    public DefaultWebRequestBuilder breakpoint(Optional<Breakpoint> breakpoint)
    {
        this.breakpoint = breakpoint;
        return this;
    }

    public WebRequest build()
    {
        Preconditions.checkNotNull(path, "The path has not been set");
        Preconditions.checkNotNull(canonicalPath, "The path has not been set");
        Preconditions.checkNotNull(baseURI, "The base URI has not been set");

        return new DefaultWebRequest(
                baseURI, canonicalPath, path, isTenantRequest, tenantPrefix,
                isApiRequest, breakpoint
        );
    }
}
