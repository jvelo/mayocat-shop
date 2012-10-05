package org.mayocat.shop.context;

import org.xwiki.component.annotation.Role;

@Role
public interface Execution
{
    Context getContext();   
}
