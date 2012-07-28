package org.mayocat.shop.configuration;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonProperty;

public class DataSourceConfiguration
{

    @Valid
    @NotNull
    @JsonProperty
    String name;

    @Valid
    @JsonProperty
    Map<String,String> properties;
    
    
    public String getName()
    {
        return name;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

}
