package org.mayocat.shop.authorization;

public enum ProductCapability implements CapabilityGroup
{

    CREATE_PRODUCT(new Capability("create_product")),
    UPDATE_PRODUCT(new Capability("update_product"));
    
    private final Capability capability;
    
    ProductCapability(Capability capability)
    {
        this.capability = capability;
    }
    
    public Capability capability() {
        return capability;
    }
}
