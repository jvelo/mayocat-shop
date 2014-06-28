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

import org.mayocat.addons.binder.BindAddonGroup;
import org.mayocat.addons.mapper.AddonGroupMapper;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.HasAddons;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 * @version $Id$
 */
public interface AddonsDAO<T extends HasAddons>
{
    @RegisterMapper(AddonGroupMapper.class)
    @SqlQuery
    (
        "SELECT * FROM addon WHERE entity_id = :entity.id"
    )
    List<AddonGroup> findAddons(@BindBean("entity") T entity);

    @RegisterMapper(AddonGroupMapper.class)
    @SqlQuery
    (
        "SELECT * FROM addon " +
        "WHERE    entity_id in ( <ids> )"
    )
    List<AddonGroup> findAllAddonsForIds(@BindIn("ids") List<UUID> ids);

    @SqlUpdate
    (
        "INSERT INTO addon " +
        "            (entity_id, " +
        "             source, " +
        "             addon_group, " +
        "             model," +
        "             value) " +
        "VALUES      (:entity.id, " +
        "             :addon.source, " +
        "             :addon.group, " +
        "             CAST (:addon.model AS json)," +
        "             CAST (:addon.value AS json)) "
    )
    void createAddonGroup(@BindBean("entity") T entity, @BindAddonGroup("addon") AddonGroup addon);

    @SqlUpdate
    (
        "UPDATE addon " +
        "SET value = CAST (:addon.value AS json), " +
        "    model = CAST (:addon.model AS json) " +
        "WHERE entity_id = :entity.id " +
        "AND   source = :addon.source " +
        "AND   addon_group = :addon.group "
    )
    void updateAddonGroup(@BindBean("entity") T entity, @BindAddonGroup("addon") AddonGroup addon);

    @SqlUpdate
    (
        "DELETE FROM addon " +
        "WHERE       addon.entity_id = :entity.id"
    )
    Integer deleteAddons(@BindBean("entity") T entity);
}
