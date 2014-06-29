/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.LocaleUtils;
import org.mayocat.model.Attachment;
import org.mayocat.model.AttachmentData;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class AttachmentMapper implements ResultSetMapper<Attachment>
{
    @Override
    public Attachment map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException
    {
        Attachment attachment = new Attachment();
        attachment.setId((UUID) resultSet.getObject("id"));
        attachment.setTitle(resultSet.getString("title"));
        attachment.setDescription(resultSet.getString("description"));
        attachment.setSlug(resultSet.getString("slug"));
        attachment.setData(new AttachmentData(resultSet.getBinaryStream("data")));
        attachment.setExtension(resultSet.getString("extension"));
        attachment.setParentId((UUID) resultSet.getObject("parent_id"));

        ObjectMapper mapper = new ObjectMapper();

        if (!Strings.isNullOrEmpty(resultSet.getString("metadata"))) {
            try {

                Map<String, Map<String, Object>> metadata = mapper.readValue(resultSet.getString("metadata"),
                        new TypeReference<Map<String, Map<String, Object>>>()
                        {
                        });
                attachment.setMetadata(metadata);
            } catch (IOException e) {
                throw new SQLException("Failed to de-serialize localization JSON data", e);
            }
        }

        if (MapperUtils.hasColumn("localization_data", resultSet) &&
                !Strings.isNullOrEmpty(resultSet.getString("localization_data")))
        {
            try {
                Map<Locale, Map<String, Object>> localizedVersions = Maps.newHashMap();
                Map[] data = mapper.readValue(resultSet.getString("localization_data"), Map[].class);
                for (Map map : data) {
                    localizedVersions.put(LocaleUtils.toLocale((String) map.get("locale")), (Map) map.get("entity"));
                }
                attachment.setLocalizedVersions(localizedVersions);
            } catch (IOException e) {
                throw new SQLException("Failed to de-serialize localization JSON data", e);
            }
        }

        return attachment;
    }
}
