/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.rest.Resource
import org.mayocat.rest.parameters.ImageOptions
import org.mayocat.rest.resources.AbstractImageResource
import org.xwiki.component.annotation.Component

import javax.servlet.ServletContext
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/marketplace/images/")
@Path("/marketplace/images/")
@CompileStatic
class MarketplaceImageWebView extends AbstractImageResource implements Resource
{
    @GET
    @Path("thumbnails/{tenant}/{slug}_{x: \\d+}_{y: \\d+}_{width: \\d+}_{height: \\d+}.{ext}")
    public Response downloadThumbnail(
            @PathParam("tenant") String tenantSlug,
            @PathParam("slug") String slug,
            @PathParam("ext") String extension,
            @PathParam("x") Integer x,
            @PathParam("y") Integer y,
            @PathParam("width") Integer width,
            @PathParam("height") Integer height,
            @Context ServletContext servletContext,
            @Context Optional<ImageOptions> imageOptions)
    {
        return super.downloadThumbnail(tenantSlug, slug, extension, x, y, width, height, servletContext, imageOptions);
    }

    @GET
    @Path("{tenant}/{slug}.{ext}")
    public Response downloadImage(
            @PathParam("tenant") String tenantSlug,
            @PathParam("slug") String slug,
            @PathParam("ext") String extension,
            @Context ServletContext servletContext,
            @Context Optional<ImageOptions> imageOptions)
    {
        return super.downloadImage(tenantSlug, slug, extension, servletContext, imageOptions);
    }
}
