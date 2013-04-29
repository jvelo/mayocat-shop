package org.mayocat.store.rdbms.dbi.dao;

import java.util.UUID;

import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.store.jdbi.mapper.AddressMapper;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(AddressMapper.class)
@UseStringTemplate3StatementLocator
public abstract class AddressDAO implements Transactional<AddressDAO>
{
    @GetGeneratedKeys
    @SqlUpdate
    public abstract UUID createAddress(@BindBean("address") Address address);
}
