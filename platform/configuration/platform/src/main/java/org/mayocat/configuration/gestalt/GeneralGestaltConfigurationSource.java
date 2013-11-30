/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.gestalt;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.general.GeneralSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("general")
public class GeneralGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private GeneralSettings generalSettings;

    @Override
    public Object get()
    {
        return generalSettings;
    }
}
