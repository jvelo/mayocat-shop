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
    private String canonicalPath;

    private String path;

    private URI baseURI;

    private Optional<Breakpoint> breakpoint = Optional.<Breakpoint>absent();

    public DefaultWebRequest(URI baseURI, String canonicalPath, String path, Optional<Breakpoint> breakpoint)
    {
        this.baseURI = baseURI;
        this.canonicalPath = canonicalPath;
        this.path = path;
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
    public URI getBaseUri()
    {
        return baseURI;
    }
}
