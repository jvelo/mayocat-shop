/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web

import groovy.transform.CompileStatic
import org.mayocat.context.WebContext
import org.mayocat.rest.Resource
import org.mayocat.shop.front.views.WebView
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/marketplace")
@Path("/marketplace")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@CompileStatic
class MarketplaceHomeWebView implements Resource
{
    @Inject
    WebContext context

    @GET
    def getHomePage()
    {
        Map<String, Object> data = new HashMap<>();

        data.put("user", context.user)

        return new WebView().data(data);
    }
}
