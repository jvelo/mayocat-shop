/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front;

import java.util.Map;

import org.xwiki.component.annotation.Role;

/**
 * A web data supplier is a component that can add (supply) data to the global data map context that will be used to
 * render a web view (either as a HTML page or a JSON response, etc.).
 *
 * @version $Id$
 */
@Role
public interface WebDataSupplier
{
    /**
     * Supplies data to the passed data map.
     *
     * @param data the global data map to supply data into
     */
    void supply(Map<String, Object> data);
}
