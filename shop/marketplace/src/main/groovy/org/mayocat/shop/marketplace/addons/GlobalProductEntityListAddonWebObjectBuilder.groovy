package org.mayocat.shop.marketplace.addons

import com.google.common.base.Optional
import org.mayocat.accounts.model.Tenant
import org.mayocat.addons.EntityListAddonWebObjectBuilder
import org.mayocat.entity.EntityData
import org.mayocat.model.Entity
import org.mayocat.shop.marketplace.web.delegate.WithProductWebObjectBuilder
import org.xwiki.component.annotation.Component

/**
 * @version $Id$
 */
@Component("global/product")
class GlobalProductEntityListAddonWebObjectBuilder
        implements WithProductWebObjectBuilder, EntityListAddonWebObjectBuilder
{
    @Override
    Object build(EntityData<Entity> entity)
    {
        Optional<Tenant> tenant = entity.getData(Tenant.class)
        return this.buildProductWebObject(tenant.orNull(), entity)
    }
}
