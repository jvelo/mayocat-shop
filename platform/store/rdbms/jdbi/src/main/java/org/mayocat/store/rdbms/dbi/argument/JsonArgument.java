package org.mayocat.store.rdbms.dbi.argument;

/**
 * @version $Id$
 */
public class JsonArgument
{
    private Object wrapped;

    public JsonArgument(Object wrapped)
    {
        this.wrapped = wrapped;
    }

    public Object getWrapped()
    {
        return wrapped;
    }
}
