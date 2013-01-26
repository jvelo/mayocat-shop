package org.mayocat.shop.rest.views;

import java.util.Map;

import org.mayocat.shop.theme.Breakpoint;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class FrontView
{
    private String layout;

    private Breakpoint breakpoint;

    private Map<String, Object> context;

    public FrontView(String layout, Breakpoint breakpoint)
    {
        this.layout = layout;
        this.breakpoint = breakpoint;
        this.context = Maps.newHashMap();
    }

    public String getLayout()
    {
        return layout;
    }

    public Breakpoint getBreakpoint()
    {
        return breakpoint;
    }

    public Map<String, Object> getContext()
    {
        return this.context;
    }

    public void putInContext(String key, Object value)
    {
        this.context.put(key, value);
    }
}
