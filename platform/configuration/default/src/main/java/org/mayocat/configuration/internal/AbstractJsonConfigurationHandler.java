/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.util.Map;

/**
 * @version $Id$
 */
public class AbstractJsonConfigurationHandler
{
    public static final String CONFIGURABLE_KEY = "configurable";

    public static final String DEFAULT_KEY = "default";

    public static final String VISIBLE_KEY = "visible";

    public static final String VALUE_KEY = "value";

    protected final Map<String, Object> platform;

    protected final Map<String, Object> tenant;

    public AbstractJsonConfigurationHandler(
            final Map<String, Object> platform, final Map<String, Object> tenant)
    {
        this.platform = platform;
        this.tenant = tenant;
    }

    protected boolean isConfigurableEntry(Map<String, Object> entry)
    {
        return entry.containsKey(CONFIGURABLE_KEY)
                && Boolean.class.isAssignableFrom(entry.get(CONFIGURABLE_KEY).getClass())
                && entry.containsKey(DEFAULT_KEY)
                && entry.containsKey(VISIBLE_KEY);
    }
}
