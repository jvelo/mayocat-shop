package org.mayocat.model;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @version $Id$
 */
public class Addon<T> implements Serializable
{
    @JsonIgnore
    private UUID entityId;

    private T value;

    private AddonSource source;

    private String group;

    private String key;

    private AddonFieldType type;

    public UUID getEntityId()
    {
        return entityId;
    }

    public void setEntityId(UUID entityId)
    {
        this.entityId = entityId;
    }

    public T getValue()
    {
        return value;
    }

    public AddonSource getSource()
    {
        return source;
    }

    public String getGroup()
    {
        return group;
    }

    public String getKey()
    {
        return key;
    }

    public AddonFieldType getType()
    {
        return type;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public void setSource(AddonSource source)
    {
        this.source = source;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setType(AddonFieldType type)
    {
        this.type = type;
    }
}
