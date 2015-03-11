/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration;

import java.io.Serializable;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.xwiki.component.annotation.Role;

/**
 * All operations configuration (settings and gestalt configuration) related.
 *
 * @version $Id$
 */
@Role
public interface ConfigurationService
{
    /**
     * Same as {@link #getSettings(org.mayocat.accounts.model.Tenant)}, for the context's tenant
     */
    Map<Class, Serializable> getSettings();

    /**
     * @param tenant the tenant to get the settings for
     * @return the whole map of {@link ExposedSettings}, merged (meaning per-tenant settings value are overriding
     *         platform defaults when they exists/are defined). Keys are individual settings classes, and values their
     *         corresponding (merged) instance.
     */
    Map<Class, Serializable> getSettings(Tenant tenant);

    /**
     * @param c the class of the settings to get.
     * @return the top level settings entry, merged, for this class, if it exists.
     * @see #getSettings()
     */
    <T extends ExposedSettings> T getSettings(Class<T> c);

    /**
     * @param c the class of the settings to get.
     * @param tenant the tenant to get the settings for
     * @return the top level settings entry, merged, for this class, if it exists.
     * @see #getSettings()
     */
    <T extends ExposedSettings> T getSettings(Class<T> c, Tenant tenant);

    /**
     * Updates the per-tenant settings for the context tenant with the passed configuration.
     *
     * @param configuration the configuration to set for the context tenant.
     */
    void updateSettings(Map<String, Serializable> configuration);

    /**
     * Updates a single module in the per-tenant settings for the context tenant, with the passed configuration.
     *
     * @param module the module to update, which is the key in the global settings map
     * @param configuration the configuration of the module to set for the context tenant.
     */
    void updateSettings(String module, Map<String, Serializable> configuration) throws NoSuchModuleException;

    /**
     * @see {@link #getSettingsAsJson()}
     *
     * Returns settings as JSON for the context tenant
     */
    Map<String, Serializable> getSettingsAsJson();

    /**
     * Same behavior as {@link #getSettings()}, but ready for JSON serialization
     *
     * @param tenant the tenant to get the settings for
     * @return the settings as a map ready for JSON serialization
     */
    Map<String, Serializable> getSettingsAsJson(Tenant tenant);

    /**
     * Same behavior as {@link #getSettings()}, but ready for JSON serialization
     *
     * @return the settings as a map ready for JSON serialization
     * @throws NoSuchModuleException when the desired module does not exist.
     */
    Map<String, Serializable> getSettingsAsJson(String moduleName) throws NoSuchModuleException;

    /**
     * @return the gestalt configuration as a map ready for JSON serialization. The gestalt configuration differes from
     *         settings as it is a ready-only, consolidated configuration that is built against different sources,
     *         including but not limited to sources based on settings.
     * @see {@link GestaltConfigurationSource}
     */
    Map<String, Serializable> getGestaltConfiguration();
}
