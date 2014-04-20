/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.flyway;

import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.ConfigurationStrategy;
import com.yammer.dropwizard.util.Generics;

/**
 * @version $Id$
 */
public abstract class FlywayBundle<T extends Configuration> implements Bundle, ConfigurationStrategy<T>
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
