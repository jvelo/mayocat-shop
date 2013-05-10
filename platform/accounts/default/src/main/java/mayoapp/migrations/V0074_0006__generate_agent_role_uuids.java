package mayoapp.migrations;

import java.util.Arrays;
import java.util.List;

import org.mayocat.flyway.migrations.AbstractUUIDGenerationMigration;

/**
 * @version $Id$
 */
public class V0074_0006__generate_agent_role_uuids extends AbstractUUIDGenerationMigration
{
    @Override
    public List<String> getTableNames()
    {
        return Arrays.asList("agent_role");
    }

    @Override
    public String getTransitionTableSuffix()
    {
        return "_with_uuid";
    }
}
