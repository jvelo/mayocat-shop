package org.mayocat.shop.rest.views;

import java.util.Map;

import org.mayocat.theme.Breakpoint;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class FrontView
{
    private String layout;

    private Breakpoint breakpoint;

    private Map<String, Object> bindings;

    public FrontView(String layout, Breakpoint breakpoint)
    {
        this.layout = layout;
        this.breakpoint = breakpoint;
        this.bindings = Maps.newHashMap();
    }

    public String getLayout()
    {
        return layout;
    }

    public Breakpoint getBreakpoint()
    {
        return breakpoint;
    }

    public Map<String, Object> getBindings()
    {
        return this.bindings;
    }

    public void putBindings(Map<String, Object> bindings)
    {
        this.bindings.putAll(bindings);
    }
}
