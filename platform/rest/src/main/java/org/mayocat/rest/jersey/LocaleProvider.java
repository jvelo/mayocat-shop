/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jersey;

import java.util.List;
import java.util.Locale;

import org.mayocat.rest.Provider;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.core.HttpContext;

/**
 * @version $Id$
 */
@Component("locale")
public class LocaleProvider extends AbstractInjectableProvider<Locale> implements Provider
{
    public LocaleProvider()
    {
        super(Locale.class);
    }

    @Override
    public Locale getValue(HttpContext c)
    {
        final List<Locale> locales = c.getRequest().getAcceptableLanguages();
        if (locales.isEmpty()) {
            return Locale.getDefault();
        }
        return locales.get(0);
    }
}

