/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
