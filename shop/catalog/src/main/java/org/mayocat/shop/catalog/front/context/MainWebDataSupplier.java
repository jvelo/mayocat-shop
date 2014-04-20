/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.context;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.context.WebContext;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.ResourceResource;
import org.xwiki.component.annotation.Component;

/**
 * Main web data supplier : contributes the theme path and a fallback for the page title to all web views.
 *
 * @version $Id$
 */
@Component("main")
public class MainWebDataSupplier implements WebDataSupplier, ContextConstants
{
    public static final String THEME_PATH = "themePath";

    @Inject
    private WebContext context;

    @Override
    public void supply(Map<String, Object> data)
    {
        data.put(THEME_PATH, ResourceResource.PATH);

        if (!data.containsKey(PAGE_TITLE)) {
            // Page title probably has been set already by when building the initial web view data :
            // for example a product page will set it to the product's title.
            // Here we handle the case where it has not been set, and set it to the name of the shop (of the tenant)
            data.put(PAGE_TITLE, context.getTenant().getName());
        }
    }
}
