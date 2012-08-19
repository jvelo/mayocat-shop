package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.mayocat.shop.model.annotation.SearchIndex;

public class LocalizedText implements Localized, Entity
{

    Long id;

    @NotNull
    @Pattern (message="Format is {entityType:field}", regexp="\\w+:\\w+")
    String key;
    
    @SearchIndex
    @NotNull
    String value;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
}
