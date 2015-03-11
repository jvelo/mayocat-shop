/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.mapper;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mayocat.model.AddonGroup;
import org.mayocat.model.AddonSource;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class AddonGroupMapper implements ResultSetMapper<AddonGroup>
{
    @Override
    public AddonGroup map(int index, ResultSet result, StatementContext context) throws SQLException
    {
        AddonGroup addonGroup = new AddonGroup();
        addonGroup.setEntityId((UUID) result.getObject("entity_id"));
        addonGroup.setGroup(result.getString("addon_group"));
        addonGroup.setSource(AddonSource.fromJson(result.getString("source")));

        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> value = mapper.readValue(result.getString("value"), new TypeReference<Map<String, Object>>(){});
            addonGroup.setValue(value);
        } catch (IOException e) {
            // Failed as a map ? it must be a sequenced addon
            try {
                List<Map<String, Object>> value = mapper.readValue(result.getString("value"),
                        new TypeReference<List<Map<String, Object>>>(){});
                addonGroup.setValue(value);
            } catch (IOException e1) {
                throw new SQLException("Failed to de-serialize value", e);
            }
        }

        try {
            Map<String, Map<String, Object>>
                    model = mapper.readValue(result.getString("model"), new TypeReference<Map<String, Object>>()
            {
            });
            addonGroup.setModel(model);
        } catch (IOException e) {
            throw new SQLException("Failed to de-serialize model", e);
        }

        return addonGroup;
    }
}
