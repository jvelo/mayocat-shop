package org.mayocat.theme;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import org.hibernate.validator.constraints.NotBlank;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.images.ImageFormatDefinition;

import javax.validation.Valid;

/**
 * @version $Id$
 */
public class ThemeDefinition
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
    private Map<String, ImageFormatDefinition> images = Maps.newHashMap();

    @Valid
    @JsonProperty
    private Map<String, Model> models = Maps.newLinkedHashMap();

    @Valid
    @JsonProperty
    private Map<String, AddonGroup> addons = Collections.emptyMap();

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Map<String, ImageFormatDefinition> getImageFormats()
    {
        return images;
    }

    public Map<String, Model> getModels()
    {
        return models;
    }

    public Map<String, AddonGroup> getAddons()
    {
        return this.addons;
    }
}
