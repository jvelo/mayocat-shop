/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.LocaleUtils;
import org.mayocat.cms.pages.model.Page;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PageMapper implements ResultSetMapper<Page>
{
    @Override
    public Page map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Page page = new Page((UUID) resultSet.getObject("id"));
        if (resultSet.getObject("published") != null) {
            page.setPublished(resultSet.getBoolean("published"));
        }
        page.setTitle(resultSet.getString("title"));
        page.setSlug(resultSet.getString("slug"));
        page.setContent(resultSet.getString("content"));

        page.setFeaturedImageId((UUID) resultSet.getObject("featured_image_id"));

        if (!Strings.isNullOrEmpty(resultSet.getString("localization_data"))) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<Locale, Map<String, Object>> localizedVersions = Maps.newHashMap();
                Map[] data = mapper.readValue(resultSet.getString("localization_data"), Map[].class);
                for (Map map : data) {
                    localizedVersions.put(LocaleUtils.toLocale((String) map.get("locale")), (Map) map.get("entity"));
                }
                page.setLocalizedVersions(localizedVersions);
            } catch (IOException e) {
                throw new SQLException("Failed to de-serialize localization JSON data", e);
            }
        }

        String model = resultSet.getString("model");
        if (!Strings.isNullOrEmpty(model)) {
            page.setModel(model);
        }

        return page;
    }
}
