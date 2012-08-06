package org.mayocat.shop.authorization.capability.shop;

import org.mayocat.shop.authorization.Capability;
import org.xwiki.component.annotation.Component;

@Component("EDIT_PRODUCT")
public class EditProduct extends Capability
{

   public EditProduct()
   {
       super("EDIT_PRODUCT");
   }
}
