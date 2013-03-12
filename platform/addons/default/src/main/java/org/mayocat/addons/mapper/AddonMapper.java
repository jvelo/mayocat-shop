package org.mayocat.addons.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.model.Addon;
import org.mayocat.model.AddonFieldType;
import org.mayocat.model.AddonSource;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class AddonMapper implements ResultSetMapper<Addon>
{
    @Override
    public Addon map(int index, ResultSet result, StatementContext ctx) throws SQLException
    {
        Addon addon;
        AddonFieldType type = AddonFieldType.fromJson(result.getString("type"));

        switch (type) {
            case STRING:
            default:
                //
                addon = new Addon<String>();
                addon.setValue(result.getString("value"));
                break;
        }

        addon.setType(type);
        addon.setName(result.getString("name"));
        addon.setSource(AddonSource.fromJson(result.getString("source")));
        addon.setHint(result.getString("hint"));
        return addon;
    }
}
