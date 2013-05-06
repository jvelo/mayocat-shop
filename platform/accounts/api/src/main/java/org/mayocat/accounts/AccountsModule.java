package org.mayocat.accounts;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.accounts.meta.TenantEntity;
import org.mayocat.meta.EntityMeta;
import org.mayocat.Module;
import org.mayocat.accounts.meta.UserEntity;

/**
 * @version $Id$
 */
public class AccountsModule implements Module
{
    private static final List<EntityMeta> entities = new ArrayList<EntityMeta>();

    static {
        entities.add(new UserEntity());
        entities.add(new TenantEntity());
    }

    @Override
    public String getName()
    {
        return "accounts";
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
