/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;

import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.shop.catalog.model.Feature;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.FeatureMapper;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * DAO for product features. See {@link Feature}
 *
 * @version $Id$
 */
@RegisterMapper(FeatureMapper.class)
@UseStringTemplate3StatementLocator
public abstract class FeatureDAO  implements EntityDAO<Feature>, Transactional<FeatureDAO>,
        AddonsDAO<Feature>, LocalizationDAO<Feature>
{

    public void createOrUpdateAddons(Feature entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }

    @SqlUpdate
    public abstract void createFeature(@BindBean("feature") Feature feature);

    @SqlQuery
    public abstract List<Feature> findAllForProduct(@BindBean("product") Product product);
}
