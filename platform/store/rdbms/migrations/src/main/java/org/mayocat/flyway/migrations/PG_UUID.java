/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.flyway.migrations;

import java.sql.SQLException;
import java.util.UUID;

import org.postgresql.util.PGobject;

/**
 * Postgres UUID PGObject
 *
 * @version $Id$
 */
public class PG_UUID extends PGobject
{
    private static final long serialVersionUID = -3049777497876782935L;

    public PG_UUID(UUID id) throws SQLException
    {
        this(id.toString());
    }

    public PG_UUID(String s) throws SQLException
    {
        super();
        this.setType("uuid");
        this.setValue(s);
    }
}
