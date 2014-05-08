/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.flyway.migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class that fixes NULL featured image id for an entity table (such as articles, pages, etc.)
 *
 * Extending class must just precise the table name to fix NULL featured image id for.
 *
 * @version $Id$
 */
public abstract class AbstractFixNullFeaturedImageMigration
{
    public void migrate(Connection connection) throws Exception
    {
        connection.prepareStatement(
                "update " + getTableName() + " as ee set featured_image_id =" +
                        " (select ae.id from entity as ae join attachment as aa on ae.id = aa.entity_id" +
                        " where ae.parent_id = ee.entity_id and lower(extension)" +
                        " in ('jpg', 'png', 'jpeg', 'gif', 'tiff', 'bmp') limit 1)" +
                        " where featured_image_id is NULL;"
        ).executeUpdate();
    }

    public abstract String getTableName();
}
