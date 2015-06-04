/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import javax.inject.Inject;
import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.context.WebContext;

/**
 * @version $Id$
 */
public abstract class AbstractGatewayFactory implements GatewayFactory
{
    private static final String PAYMENTS_DIRECTORY = "payments";
    private static final String TENANTS_DIRECTORY = "tenants";

    @Inject
    private FilesSettings filesSettings;

    @Inject
    private WebContext context;

    /**
     * @return this gateway global (server-level) configuration file
     *
     * @param fileName the name of the file to retrieve
     */
    protected File getGlobalConfigurationFile(String fileName) {
        return filesSettings.getPermanentDirectory()
                .resolve(PAYMENTS_DIRECTORY)
                .resolve(this.getId())
                .resolve(fileName).toFile();
    }

    /**
     * @return this gateway global (server-level) configuration file
     *
     * @param fileName the name of the file to retrieve
     */
    protected File getTenantConfigurationFile(String fileName) {
        return filesSettings.getPermanentDirectory()
                .resolve(TENANTS_DIRECTORY)
                .resolve(this.context.getTenant().getSlug())
                .resolve(PAYMENTS_DIRECTORY)
                .resolve(this.getId())
                .resolve(fileName).toFile();
    }

}
