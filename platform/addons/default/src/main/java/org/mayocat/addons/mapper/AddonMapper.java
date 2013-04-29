package org.mayocat.addons.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
        addon.setKey(result.getString("addon_key"));
        addon.setSource(AddonSource.fromJson(result.getString("source")));
        addon.setGroup(result.getString("addon_group"));
        addon.setEntityId((UUID)result.getObject("entity_id"));

        return addon;
    }
}
