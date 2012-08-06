package org.mayocat.shop.authorization.capability.shop;

import org.mayocat.shop.authorization.Capability;
import org.xwiki.component.annotation.Component;

@Component("ADD_PRODUCT")
public class AddProduct extends Capability
{

    public AddProduct()
    {
        super("ADD_PRODUCT");
    }

}
