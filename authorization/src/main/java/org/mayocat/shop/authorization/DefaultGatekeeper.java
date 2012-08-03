package org.mayocat.shop.authorization;

import org.mayocat.shop.model.User;

public class DefaultGatekeeper implements Gatekeeper
{

    @Override
    public boolean hasCapability(User user, Capability capability)
    {
        Capability cap = ProductCapability.CREATE_PRODUCT.capability();
        return false;
    }

}
