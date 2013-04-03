package org.mayocat.session.cookies;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.mayocat.session.Session;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

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
}
