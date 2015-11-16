/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.addons

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.addons.EntityListAddonWebObjectBuilder
import org.mayocat.entity.EntityData
import org.mayocat.model.Entity
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.marketplace.web.delegate.WithProductWebObjectBuilder
import org.xwiki.component.annotation.Component

/**
 * @version $Id$
 */
@Component("global/product")
@CompileStatic
class GlobalProductEntityListAddonWebObjectBuilder
        implements WithProductWebObjectBuilder, EntityListAddonWebObjectBuilder
{
    @Override
    Object build(EntityData<Entity> entity)
    {
        Optional<Tenant> tenant = entity.getData(Tenant.class)
        return this.buildProductWebObject(tenant.orNull(), entity as EntityData<Product>)
    }
}
