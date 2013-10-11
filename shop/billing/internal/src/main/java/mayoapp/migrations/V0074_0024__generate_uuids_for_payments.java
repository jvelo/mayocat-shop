package mayoapp.migrations;

import java.util.Arrays;
import java.util.List;

import org.mayocat.flyway.migrations.AbstractUUIDGenerationMigration;

/**
 * @version $Id$
 */
public class V0074_0024__generate_uuids_for_payments extends AbstractUUIDGenerationMigration
{
    @Override
    public List<String> getTableNames()
    {
        return Arrays.asList("payment_operation");
    }

    @Override
    public String getTransitionTableSuffix()
    {
        return "_with_uuid";
    }

    @Override
    public String getIdField()
    {
        return "operation_id";
    }

    @Override
    public String getUUIDField()
    {
        return "operation_id_with_uuid";
    }
}
