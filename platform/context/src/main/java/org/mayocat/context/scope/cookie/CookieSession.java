/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context.scope.cookie;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.mayocat.context.scope.Session;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class CookieSession implements Session
{
    private static final long serialVersionUID = 2197579816031332610L;

    private Map<String, Serializable> attributes = Maps.newHashMap();

    @Override
    public boolean isEmpty()
    {
        return attributes.keySet().isEmpty();
    }

    @Override
    public Set<String> getAttributeNames()
    {
        return attributes.keySet();
    }

    @Override
    public Object getAttribute(String key)
    {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Serializable value)
    {
        this.attributes.put(key, value);
    }

    @Override
    public void removeAttribute(String key)
    {
        this.attributes.remove(key);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CookieSession other = (CookieSession) obj;

        return Objects.equal(this.attributes, other.attributes);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.attributes
        );
    }
}
