/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.context;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.context.WebContext;
import org.mayocat.context.scope.Flash;
import org.mayocat.shop.front.WebDataSupplier;
import org.xwiki.component.annotation.Component;

/**
 * Supplies flash context data to all web views : if the flash context is not empty, we put its data map in in the web
 * view map.
 *
 * @version $Id$
 */
@Component("flash")
public class FlashContextWebDataSupplier implements WebDataSupplier
{
    @Inject
    private WebContext context;

    @Override
    public void supply(Map<String, Object> data)
    {
        Flash flash = context.getFlash();
        if (!flash.isEmpty()) {
            Map<String, Object> flashMap = new HashMap();
            for (String attribute : flash.getAttributeNames()) {
                flashMap.put(attribute, flash.getAttribute(attribute));
            }
            data.put("flash", flashMap);
        }
    }
}
