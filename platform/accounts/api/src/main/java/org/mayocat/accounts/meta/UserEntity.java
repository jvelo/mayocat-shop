package org.mayocat.accounts.meta;

import org.mayocat.accounts.model.User;
import org.mayocat.meta.EntityMeta;

/**
 * @version $Id$
 */
public class UserEntity implements EntityMeta
{
    public static final String ID = "user";

    public static final String PATH = "users";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return User.class;
    }
}
