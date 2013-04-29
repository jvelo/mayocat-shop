package mayoapp.migrations;

import java.util.Arrays;
import java.util.List;

import org.mayocat.flyway.migrations.AbstractUUIDGenerationMigration;

/**
 * @version $Id$
 */
public class V0074_0023__generate_uuids_for_addresses extends AbstractUUIDGenerationMigration
{
    @Override
    public List<String> getTableNames()
    {
        return Arrays.asList("address");
    }

    @Override
    public String getTransitionTableSuffix()
    {
        return "_with_uuid";
    }

    @Override
    public String getIdField()
    {
        return "address_id";
    }

    @Override
    public String getUUIDField()
    {
        return "address_id_with_uuid";
    }
}
