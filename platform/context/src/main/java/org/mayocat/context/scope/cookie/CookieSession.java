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
