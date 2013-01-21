package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.util.Map;

import org.mayocat.shop.store.rdbms.dbi.jointype.EntityAndCountsJoinRow;

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
                String countName = getCountName(key);
                countBuilder.put(countName, (Long) rowData.get(key));

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
        return (rowKey.toLowerCase().startsWith("count(") || rowKey.toLowerCase().startsWith(".count("))
                && rowKey.endsWith(")");
    }

    private String getCountName(String rowKey)
    {
        try {
            String count;
            if (rowKey.toLowerCase().startsWith("count(")) {
                count = rowKey.substring(6);
            } else {
                count = rowKey.substring(7);
            }
            count = count.substring(0, count.length() - 1);
            return count;
        } catch (Exception e) {
            // Do nothing
            return rowKey;
        }
    }

}
