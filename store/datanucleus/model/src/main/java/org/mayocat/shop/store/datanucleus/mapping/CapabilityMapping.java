package org.mayocat.shop.store.datanucleus.mapping;

import org.datanucleus.store.mapped.mapping.ObjectAsStringMapping;
import org.mayocat.shop.authorization.Capability;

public class CapabilityMapping extends ObjectAsStringMapping
{

    @Override
    public Class getJavaType()
    {
        return Capability.class;
    }

    @Override
    protected String objectToString(Object object)
    {
        if (object instanceof Capability) {
            return ((Capability) object).getName();
        }
        else {
            throw new RuntimeException("Attempting to map a capability with something else");
        }
    }

    @Override
    protected Object stringToObject(final String datastoreValue)
    {
        return new Capability(){

            @Override
            public String getName()
            {
                return datastoreValue;
            }
        };
    }

}
