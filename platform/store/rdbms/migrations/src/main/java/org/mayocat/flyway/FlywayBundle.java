/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.flyway;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.util.Generics;

/**
 * @version $Id$
 */
public abstract class FlywayBundle<T extends Configuration> implements Bundle, DatabaseConfiguration<T>
{
    @Override
    public final void initialize(Bootstrap<?> bootstrap)
    {
        final Class<T> klass = Generics.getTypeParameter(getClass(), Configuration.class);
        bootstrap.addCommand(new FlywayMigrateCommand(this, klass));
    }

    @Override
    public final void run(Environment environment)
    {
        // doing nothing
    }
}
