/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.util.Map;

import com.google.common.reflect.TypeToken;

/**
 * @version $Id$
 */
public class ConfigurationMerger
{
    public <T> T merge(T object, Map<String, Object> json)
    {
        return (T) this.mergeInternal(getConfigurationClass(object), object, json);
    }

    private Object mergeInternal(Class klass, Object object, Map<String, Object> json)
    {
        try {
            Object merged = klass.newInstance();

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }
        return object;
    }

    public <T> Class getConfigurationClass(Object object)
    {
        TypeToken<T> type = new TypeToken<T>(object.getClass()) {};
        return type.getRawType();
    }
}
