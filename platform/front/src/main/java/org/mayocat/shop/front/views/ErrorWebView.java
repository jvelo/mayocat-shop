/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.views;

import java.nio.file.Paths;

/**
 * Web views for errors. Templates names correspond to the HTTP status code for that error, example : 500.html for
 * 500 errors, 404.html for 400 errors, etc.
 *
 * @version $Id$
 */
public class ErrorWebView extends WebView
{
    public ErrorWebView()
    {
        this.with(Option.FALLBACK_ON_GLOBAL_TEMPLATES);
    }

    public ErrorWebView status(int status)
    {
        this.template(Paths.get(status + ".html"));
        return this;
    }
}
