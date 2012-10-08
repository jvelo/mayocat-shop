package org.mayocat.shop.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Shop implements Entity
{
    @JsonIgnore
    Long id;

    String name;

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
