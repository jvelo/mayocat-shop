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

    private Map<String, Object> context;

    public FrontView(String layout, Optional<String> model, Breakpoint breakpoint)
    {
        this.layout = layout;
        this.model = model;
        this.breakpoint = breakpoint;
        this.context = Maps.newHashMap();
    }

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

    public void putContext(Map<String, Object> context)
    {
        this.context.putAll(context);
    }

    public Optional<String> getModel()
    {
        return model;
    }
}
