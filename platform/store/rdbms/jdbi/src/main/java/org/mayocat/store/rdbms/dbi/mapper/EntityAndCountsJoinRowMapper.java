/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.mapper;

import java.util.Map;

import org.mayocat.store.rdbms.dbi.jointype.EntityAndCountsJoinRow;

import com.google.common.collect.ImmutableMap;

/**
 * @version $Id$
 */
public class EntityAndCountsJoinRowMapper extends BaseMapMapper<EntityAndCountsJoinRow>
{
    @Override
    protected EntityAndCountsJoinRow mapInternal(int index, final Map<String, Object> rowData)
    {
        EntityAndCountsJoinRow row = new EntityAndCountsJoinRow();
        ImmutableMap.Builder dataBuilder = new ImmutableMap.Builder<String, Object>();
        ImmutableMap.Builder countBuilder = new ImmutableMap.Builder<String, Long>();

        for (String key : rowData.keySet()) {
            if (isCountKey(key)) {
                // This is a count row;
                countBuilder.put(key, (Long) rowData.get(key));
            } else if (rowData.get(key) != null) {
                dataBuilder.put(key, rowData.get(key));
            }
        }

        row.setCounts(countBuilder.build());
        row.setEntityData(dataBuilder.build());

        return row;
    }

    private boolean isCountKey(String rowKey)
    {
        return (rowKey.toLowerCase().startsWith("_count"));
    }
}
