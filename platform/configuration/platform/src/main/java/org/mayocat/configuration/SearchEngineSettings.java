package org.mayocat.configuration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;


public class SearchEngineSettings
{
    @Valid
    @JsonProperty
    private String name = "elasticsearch";

    public String getName()
    {
        return name;
    }
}
