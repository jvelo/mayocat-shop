package org.mayocat.shop.authorization.capability.shop;

import org.mayocat.shop.authorization.Capability;
import org.xwiki.component.annotation.Component;

@Component("EDIT_PRODUCT")
public class EditProduct implements Capability
{

    @Override
    public String getName()
    {
        return "EDIT_PRODUCT";
    }

}
