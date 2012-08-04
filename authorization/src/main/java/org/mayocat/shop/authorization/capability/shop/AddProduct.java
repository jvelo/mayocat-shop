package org.mayocat.shop.authorization.capability.shop;

import org.mayocat.shop.authorization.Capability;
import org.xwiki.component.annotation.Component;

@Component("ADD_PRODUCT")
public class AddProduct implements Capability
{

    @Override
    public String getName()
    {
        return "ADD_PRODUCT";
    }

}
