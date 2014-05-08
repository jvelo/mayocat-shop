/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.EntityList;
import org.mayocat.store.rdbms.dbi.mapper.EntityListMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * JDBI DAO for {@link EntityList}
 *
 * @version $Id$
 */
@UseStringTemplate3StatementLocator
@RegisterMapper(EntityListMapper.class)
public interface EntityListDAO extends EntityDAO<EntityList>, Transactional<EntityListDAO>
{
    @SqlUpdate
    void createEntityList(@BindBean("list") EntityList list);

    @SqlUpdate
    Integer updateEntityList(@BindBean("list") EntityList list);

    @SqlQuery
    List<EntityList> findByHint(@Bind("hint") String hint, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    EntityList findByHintAndParentId(@Bind("hint") String hint, @Bind("parent") UUID parent);

    @SqlUpdate
    void addEntityToList(@Bind("listId") UUID listId, @Bind("entityId") UUID entityId);

    @SqlUpdate
    void removeEntityFromList(@Bind("listId") UUID listId, @Bind("entityId") UUID entityId);
}
