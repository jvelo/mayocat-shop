/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.configuration.source;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("catalog")
public class CatalogGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private CatalogSettings catalogSettings;

    @Override
    public Object get()
    {
        return catalogSettings;
    }
}
