package org.mayocat.shop.configuration;

import javax.validation.Valid;

import org.codehaus.jackson.annotate.JsonProperty;

public class SearchEngineConfiguration
{
    @Valid
    @JsonProperty
    private String name = "elasticsearch";

    public String getName()
    {
        return name;
    }
}
