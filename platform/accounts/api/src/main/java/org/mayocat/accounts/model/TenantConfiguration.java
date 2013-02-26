package org.mayocat.accounts.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class TenantConfiguration implements Map<String, Object>
{
    private Map<String, Object> data;

    private final Integer version;

    public static final Integer CURRENT_VERSION = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TenantConfiguration()
    {
        this(CURRENT_VERSION);
    }

    public TenantConfiguration(final Integer version)
    {
        this(version, Maps.<String, Object>newHashMap());
    }

    public TenantConfiguration(final Integer version, final Map<String, Object> data)
    {
        this.version = version;
        this.data = data;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer getVersion()
    {
        return this.version;
    }

    public Map<String, Object> getData()
    {
        return ImmutableMap.<String, Object>builder().putAll(data).build();
    }

    @Override
    public int size()
    {
        return this.data.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.data.isEmpty();
    }

    @Override
    public boolean containsKey(Object o)
    {
        return this.data.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o)
    {
        return this.data.containsValue(o);
    }

    @Override
    public Object get(Object o)
    {
        return this.data.get(o);
    }

    @Override
    public Object put(String s, Object o)
    {
        return this.data.put(s, o);
    }

    @Override
    public Object remove(Object o)
    {
        return this.data.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ?> map)
    {
        this.data.putAll(map);
    }

    @Override
    public void clear()
    {
        this.data.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return this.data.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return this.data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return this.data.entrySet();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
