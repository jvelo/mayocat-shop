package org.mayocat.shop.context.internal;

import javax.inject.Singleton;

import org.mayocat.shop.context.Context;
import org.mayocat.shop.context.Execution;
import org.xwiki.component.annotation.Component;

@Component
@Singleton
public class DefaultExecution implements Execution
{
    private ThreadLocal<Context> context = new ThreadLocal<Context>();

    @Override
    public Context getContext()
    {
        return this.context.get();
    }

    public void setContext(Context context)
    {
        this.context.set(context);
    }
}
