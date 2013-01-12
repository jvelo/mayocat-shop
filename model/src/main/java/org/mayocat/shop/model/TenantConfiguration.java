package org.mayocat.shop.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * @version $Id$
 */
public class TenantConfiguration implements Multimap<String, Object>
{
    private Multimap<String, Object> data;

    private final Integer version;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TenantConfiguration(final Integer version)
    {
        this.version = version;
        this.data = HashMultimap.create();
    }

    public TenantConfiguration(final Integer version, final Multimap<String, Object> data)
    {
        this.version = version;
        this.data = data;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer getVersion()
    {
        return this.version;
    }

    public Multimap<String, Object> getData()
    {
        return new ImmutableMultimap.Builder<String, Object>().putAll(data).build();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    public boolean containsKey(@Nullable Object o)
    {
        return this.data.containsKey(o);
    }

    @Override
    public boolean containsValue(@Nullable Object o)
    {
        return this.data.containsValue(o);
    }

    @Override
    public boolean containsEntry(@Nullable Object o, @Nullable Object o2)
    {
        return this.data.containsEntry(o, o2);
    }

    @Override
    public boolean put(@Nullable String s, @Nullable Object o)
    {
        return this.data.put(s, o);
    }

    @Override
    public boolean remove(@Nullable Object o, @Nullable Object o2)
    {
        return this.data.remove(o, o2);
    }

    @Override
    public boolean putAll(@Nullable String s, Iterable<?> objects)
    {
        return this.data.putAll(s, objects);
    }

    @Override
    public boolean putAll(Multimap<? extends String, ?> multimap)
    {
        return this.data.putAll(multimap);
    }

    @Override
    public Collection<Object> replaceValues(@Nullable String s, Iterable<?> objects)
    {
        return this.data.replaceValues(s, objects);
    }

    @Override
    public Collection<Object> removeAll(@Nullable Object o)
    {
        return this.data.removeAll(o);
    }

    @Override
    public void clear()
    {
        this.data.clear();
    }

    @Override
    public Collection<Object> get(@Nullable String s)
    {
        return this.data.get(s);
    }

    @Override
    public Set<String> keySet()
    {
        return this.data.keySet();
    }

    @Override
    public Multiset<String> keys()
    {
        return this.data.keys();
    }

    @Override
    public Collection<Object> values()
    {
        return this.data.values();
    }

    @Override
    public Collection<Map.Entry<String, Object>> entries()
    {
        return this.data.entries();
    }

    @Override
    public Map<String, Collection<Object>> asMap()
    {
        return this.data.asMap();
    }
}
