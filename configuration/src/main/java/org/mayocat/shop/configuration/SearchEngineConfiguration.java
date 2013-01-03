package org.mayocat.shop.configuration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;


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
