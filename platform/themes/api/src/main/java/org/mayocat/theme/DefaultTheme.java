package org.mayocat.theme;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.mayocat.configuration.thumbnails.Dimensions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class DefaultTheme implements Theme
{
    @Valid
    @JsonProperty
    private String name;

    @Valid
    @JsonProperty
    private Map<String, Dimensions> thumbnails = Maps.newHashMap();

    @Valid
    @JsonProperty
    private List<Model> models;


    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Map<String, Dimensions> getThumbnails()
    {
        return thumbnails;
    }

    @Override
    public List<Model> getModels()
    {
        return models;
    }
}
