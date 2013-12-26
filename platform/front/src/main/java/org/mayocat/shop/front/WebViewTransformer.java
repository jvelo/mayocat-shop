/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.front.views.WebView;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface WebViewTransformer
{
    void transform(WebView view);
}
