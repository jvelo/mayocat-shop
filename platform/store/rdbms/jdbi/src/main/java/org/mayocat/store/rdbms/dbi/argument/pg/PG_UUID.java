/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.argument.pg;

/**
 * Postgres UUID jdbc object
 *
 * @version $Id$
 */
public class PG_UUID extends org.postgresql.util.PGobject
{
    public static final String UUID_TYPE = "uuid";

    public PG_UUID(String s) throws java.sql.SQLException
    {
        super();
        this.setType(UUID_TYPE);
        this.setValue(s);
    }
}

