/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.jdbi.mapper.PageMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(PageMapper.class)
@UseStringTemplate3StatementLocator
public abstract class PageDAO implements EntityDAO<Page>, Transactional<PageDAO>,
    PositionedDAO<Page>, AddonsDAO<Page>, LocalizationDAO<Page>
{
    @SqlQuery
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Page> findAllRootPages(@BindBean("tenant") Tenant tenant);

    @SqlUpdate
    public abstract void createPage(@Bind("position") Integer position, @BindBean("page") Page page);

    @SqlUpdate
    public abstract Integer updatePage(@BindBean("page") Page page);

    public void createOrUpdateAddons(Page entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
