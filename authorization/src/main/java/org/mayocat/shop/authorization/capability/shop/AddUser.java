package org.mayocat.shop.authorization.capability.shop;

import org.mayocat.shop.authorization.Capability;
import org.xwiki.component.annotation.Component;

@Component("ADD_USER")
public class AddUser implements Capability
{

    @Override
    public String getName()
    {
        return "ADD_USER";
    }

}
