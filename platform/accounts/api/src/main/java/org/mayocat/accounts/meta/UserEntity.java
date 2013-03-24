package org.mayocat.accounts.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.accounts.model.User;

/**
 * @version $Id$
 */
public class UserEntity implements EntityMeta
{
    @Override
    public String getEntityName()
    {
        return "user";
    }

    @Override
    public Class getEntityClass()
    {
        return User.class;
    }
}
