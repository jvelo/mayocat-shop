package mayoapp.migrations;

import java.util.Arrays;
import java.util.List;

import org.mayocat.flyway.migrations.AbstractUUIDGenerationMigration;

/**
 * @version $Id$
 */
public class V0074_0002__generate_uuids extends AbstractUUIDGenerationMigration
{
    @Override
    public List<String> getTableNames()
    {
        return Arrays.asList("configuration", "tenant", "entity", "translation");
    }

    @Override
    public String getTransitionTableSuffix()
    {
        return "_with_uuid";
    }
}
