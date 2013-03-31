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
    private static final long serialVersionUID = -6683869045957617693L;

    private Map<String, Object> attributes = Maps.newHashMap();

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
    public void setAttribute(String key, Object value)
    {
        this.attributes.put(key, value);
    }
}
