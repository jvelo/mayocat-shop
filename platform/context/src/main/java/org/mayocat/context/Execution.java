package org.mayocat.context;

import org.xwiki.component.annotation.Role;

@Role
public interface Execution
{
    Context getContext();
    
    void setContext(Context context);
}
