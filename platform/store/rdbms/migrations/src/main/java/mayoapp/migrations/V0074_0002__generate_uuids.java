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
