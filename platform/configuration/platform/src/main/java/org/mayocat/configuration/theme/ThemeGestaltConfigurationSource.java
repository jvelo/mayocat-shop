/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.theme;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.theme.ThemeDefinition;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

/**
 * @version $Id$
 */
@Component("theme")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class ThemeGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private ThemeSettings themeSettings;

    private List<ThemeDefinition> themes;

    @Override
    public Object get()
    {
        return themeSettings;
    }
}
