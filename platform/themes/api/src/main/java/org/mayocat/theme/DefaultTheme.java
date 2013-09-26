package org.mayocat.theme;

import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.thumbnails.ThumbnailDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class DefaultTheme implements Theme
{
    @Valid
    @NotBlank
    @JsonProperty
    private String name;

    @Valid
    @JsonProperty
    private String description = "";

    @Valid
    @JsonProperty
    private Map<String, ThumbnailDefinition> thumbnails = Maps.newHashMap();

    @Valid
    @JsonProperty
    private Map<String, Model> models = Maps.newLinkedHashMap();

    @Valid
    @JsonProperty
    private Map<String, AddonGroup> addons = Collections.emptyMap();

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public Map<String, ThumbnailDefinition> getThumbnails()
    {
        return thumbnails;
    }

    @Override
    public Map<String, Model> getModels()
    {
        return models;
    }

    @Override
    public Map<String, AddonGroup> getAddons()
    {
        return this.addons;
    }
}
