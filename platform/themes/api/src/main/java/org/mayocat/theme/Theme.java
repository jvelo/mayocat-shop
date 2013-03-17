package org.mayocat.theme;

import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.thumbnails.Dimensions;

/**
 * @version $Id$
 */
public interface Theme
{
    String getName();

    String getDescription();

    Map<String, Dimensions> getThumbnails();

    List<Model> getModels();

    Map<String, AddonGroup> getAddons();
}
