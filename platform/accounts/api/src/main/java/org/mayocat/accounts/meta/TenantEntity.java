package org.mayocat.accounts.meta;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.meta.EntityMeta;

/**
 * @version $Id$
 */
public class TenantEntity  implements EntityMeta
{
    public static final String ID = "tenant";

    public static final String PATH = "tenants";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return Tenant.class;
    }
}
