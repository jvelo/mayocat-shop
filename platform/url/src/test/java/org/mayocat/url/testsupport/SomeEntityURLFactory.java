/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url.testsupport;

import java.net.MalformedURLException;
import java.net.URL;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.url.EntityURLFactory;
import org.mayocat.url.URLType;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class SomeEntityURLFactory implements EntityURLFactory<SomeEntity>
{
    @Override
    public URL create(SomeEntity entity, Tenant tenant, URLType type)
    {
        try {
            return new URL("http://perdu.com");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL create(SomeEntity entity, Tenant tenant)
    {
        try {
            return new URL("http://api.perdu.com");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL create(SomeEntity entity)
    {
        try {
            return new URL("http://perdu.com");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
