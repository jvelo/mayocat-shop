/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.addons.model.AddonGroupDefinition
import org.mayocat.addons.model.BaseProperties
import org.mayocat.configuration.PlatformSettings
import org.mayocat.model.AddonGroup
import org.mayocat.model.AddonSource
import org.mayocat.theme.ThemeDefinition

import static org.mayocat.addons.util.AddonUtils.findAddonGroupDefinition

/**
 * @version $Id$
 */
@CompileStatic
class AddonGroupApiObject
{
    String group

    String source

    Object value

    Map<String, Map<String, Object>> model

    static AddonGroupApiObject forAddonGroup(AddonGroup addonGroup) {
        new AddonGroupApiObject([
                group: addonGroup.group,
                source: addonGroup.source.toJson(),
                value: addonGroup.value,
                model: addonGroup.model
        ])
    }

    static Map<String, AddonGroup> toAddonGroupMap(Map<String, AddonGroupApiObject> addons,
            PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition)
    {
        Map<String, AddonGroup> entityAddons = [:]
        for (AddonGroupApiObject addonGroupApiObject : addons.values()) {
            Optional<AddonGroupDefinition> groupDefinition;
            if (themeDefinition.isPresent()) {
                groupDefinition = findAddonGroupDefinition(addonGroupApiObject.group, platformSettings.addons,
                        themeDefinition.get().addons)
            } else {
                groupDefinition = findAddonGroupDefinition(addonGroupApiObject.group, platformSettings.addons)
            }

            AddonGroup addonGroup = addonGroupApiObject.toAddonGroup()

            if (groupDefinition.isPresent() &&
                    !groupDefinition.get().properties.containsKey(BaseProperties.READ_ONLY))
            {
                // - Addon groups for which no definition can be found are ignored
                // - Addon groups declared "Read only" are ignored : they can't be updated via this API !
                entityAddons.put(addonGroup.group, addonGroup)
            }
        }
        entityAddons
    }

    AddonGroup toAddonGroup()
    {
        AddonGroup addonGroup = new AddonGroup();
        addonGroup.with {
            setGroup this.group
            setSource AddonSource.fromJson(this.source)
            setValue this.value
            setModel this.model
        }
        addonGroup
    }
}
