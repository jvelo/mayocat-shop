/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration;

import org.xwiki.component.annotation.Role;

/**
 * The "gestalt" configuration is a read-only cross-concern consolidated configuration that represents the configuration
 * of the application as a whole ; taking into account various aspects like settings (platform settings and per-tenant
 * settings), the environment, etc. A gestalt configuration source is a contributor to this global configuration object
 * that provides a top level entry in the global gestalt configuration object to be built. The keys under which such
 * objects will be set are the components hints of this interface implementations. When a source returns an instance of
 * {@link ExposedSettings}, the settings object added to the gestalt configuration will be the merged
 * version (in which per-tenant settings are merged over the platform settings, when the former exists).
 *
 * @version $Id$
 */
@Role
public interface GestaltConfigurationSource
{
    /**
     * @return the configuration object to contribute to the gestalt configuration. If an instance of {@link
     *         ExposedSettings}, then the per-tenant settings will be automatically merged in when they
     *         exists (the gestalt source is not responsible for doing this).
     */
    Object get();
}
