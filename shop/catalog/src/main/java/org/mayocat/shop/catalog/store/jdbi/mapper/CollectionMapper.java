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

import org.apache.commons.lang3.LocaleUtils;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.store.rdbms.dbi.mapper.MapperUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class CollectionMapper implements ResultSetMapper<Collection>
{
    @Override
    public Collection map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        Collection collection = new Collection((UUID) result.getObject("id"));
        collection.setSlug(result.getString("slug"));
        collection.setTitle(result.getString("title"));
        collection.setDescription(result.getString("description"));
        collection.setFeaturedImageId((UUID) result.getObject("featured_image_id"));

        if (MapperUtils.hasColumn("localization_data", result) &&
                !Strings.isNullOrEmpty(result.getString("localization_data")))
        {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<Locale, Map<String, Object>> localizedVersions = Maps.newHashMap();
                Map[] data = mapper.readValue(result.getString("localization_data"), Map[].class);
                for (Map map : data) {
                    localizedVersions.put(LocaleUtils.toLocale((String) map.get("locale")), (Map) map.get("entity"));
                }
                collection.setLocalizedVersions(localizedVersions);
            } catch (IOException e) {
                throw new SQLException("Failed to de-serialize localization JSON data", e);
            }
        }

        return collection;
    }
}
