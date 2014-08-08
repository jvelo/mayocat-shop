/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.search;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.search.elasticsearch.AbstractGenericEntityMappingGenerator;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("tenant")
public class TenantEntityMappingGenerator extends AbstractGenericEntityMappingGenerator
{
    @Override
    public Class forClass()
    {
        return Tenant.class;
    }
}
