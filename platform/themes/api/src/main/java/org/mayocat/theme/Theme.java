package org.mayocat.theme;

import java.util.Map;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.thumbnails.ThumbnailDefinition;

/**
 * @version $Id$
 */
public interface Theme
{
    String getName();

    String getDescription();

    Map<String, ThumbnailDefinition> getThumbnails();

    Map<String, Model> getModels();

    Map<String, AddonGroup> getAddons();
}
