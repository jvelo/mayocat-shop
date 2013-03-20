package org.mayocat.rest.views;

import java.util.Map;

import org.mayocat.theme.Breakpoint;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class FrontView
{
    private String layout;

    private Optional<String> model = Optional.absent();

    private Breakpoint breakpoint;

    private Map<String, Object> bindings;

    public FrontView(String layout, Optional<String> model, Breakpoint breakpoint)
    {
        this.layout = layout;
        this.model = model;
        this.breakpoint = breakpoint;
        this.bindings = Maps.newHashMap();
    }

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

    public Optional<String> getModel()
    {
        return model;
    }
}
