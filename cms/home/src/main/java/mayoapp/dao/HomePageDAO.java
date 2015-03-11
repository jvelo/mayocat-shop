/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.UUID;

import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.cms.home.model.HomePage;
import org.mayocat.cms.home.store.jdbi.mapper.HomePageMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

/**
 * @version $Id$
 */
@RegisterMapper(HomePageMapper.class)
public abstract class HomePageDAO
        implements EntityDAO<HomePage>, Transactional<HomePageDAO>, AddonsDAO<HomePage>, LocalizationDAO<HomePage>
{

    @SqlQuery
    (
        "SELECT entity.*, localization_data(entity.id) FROM entity " +
        "WHERE entity.slug = 'home' AND entity.type = 'home' AND tenant_id IS NULL"
    )
    public abstract HomePage find();

    @SqlQuery
    (
        "SELECT entity.*, localization_data(entity.id) FROM entity " +
        "WHERE entity.slug = 'home' AND entity.type = 'home' AND tenant_id IS NOT DISTINCT FROM :tenantId"
    )
    public abstract HomePage find(@Bind("tenantId") UUID tenantId);

    public void createOrUpdateAddons(HomePage entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
