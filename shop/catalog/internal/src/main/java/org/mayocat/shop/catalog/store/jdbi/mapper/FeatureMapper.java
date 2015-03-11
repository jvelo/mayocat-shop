/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Feature;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.rdbms.dbi.mapper.MapperUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * JDBI mapper for {@link Feature}
 *
 * @version $Id$
 */
public class FeatureMapper implements ResultSetMapper<Feature>
{
    @Override
    public Feature map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Feature feature = new Feature();
        feature.setId((UUID) resultSet.getObject("id"));
        feature.setParentId((UUID) resultSet.getObject("parent_id"));
        feature.setSlug(resultSet.getString("slug"));
        feature.setTitle(resultSet.getString("title"));
        feature.setFeature(resultSet.getString("feature"));
        feature.setFeatureSlug(resultSet.getString("feature_slug"));

        if (MapperUtils.hasColumn("localization_data", resultSet) &&
                !Strings.isNullOrEmpty(resultSet.getString("localization_data")))
        {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<Locale, Map<String, Object>> localizedVersions = Maps.newHashMap();
                Map[] data = mapper.readValue(resultSet.getString("localization_data"), Map[].class);
                for (Map map : data) {
                    localizedVersions.put(Locale.forLanguageTag((String) map.get("locale")), (Map) map.get("entity"));
                }
                feature.setLocalizedVersions(localizedVersions);
            } catch (IOException e) {
                throw new SQLException("Failed to de-serialize localization JSON data", e);
            }
        }

        return feature;
    }
}
