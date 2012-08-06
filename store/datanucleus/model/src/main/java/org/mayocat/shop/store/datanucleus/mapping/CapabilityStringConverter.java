package org.mayocat.shop.store.datanucleus.mapping;

import org.datanucleus.store.types.converters.TypeConverter;
import org.mayocat.shop.authorization.Capability;

public class CapabilityStringConverter implements TypeConverter<Capability, String>
{

    @Override
    public String toDatastoreType(Capability capability)
    {
        if (capability == null) {
            return null;            
        }
        return capability.toString();
    }

    @Override
    public Capability toMemberType(String string)
    {
        if (string == null)
        {
            return null;
        }
        return new Capability(string);
    }

}
