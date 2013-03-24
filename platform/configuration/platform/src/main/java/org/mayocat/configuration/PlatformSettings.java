package org.mayocat.configuration;

import java.util.Map;

import javax.validation.Valid;

import org.mayocat.configuration.thumbnails.ThumbnailDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PlatformSettings
{
    @Valid
    @JsonProperty
    private Map<String, ThumbnailDefinition> thumbnails = Maps.newHashMap();

    public Map<String, ThumbnailDefinition> getThumbnails()
    {
        return thumbnails;
    }
}
