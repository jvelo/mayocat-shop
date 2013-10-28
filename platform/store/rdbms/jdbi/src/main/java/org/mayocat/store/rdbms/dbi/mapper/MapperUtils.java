package org.mayocat.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Utility functions for mappers
 *
 * @version $Id$
 */
public class MapperUtils
{
    public static boolean hasColumn(String columnName, ResultSet resultSet)
    {
        ResultSetMetaData meta = null;
        try {
            meta = resultSet.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i < columnCount + 1; i++) {
                if (meta.getColumnName(i).equals(columnName)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // Ignore
        }
        return false;
    }
}
