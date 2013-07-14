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
