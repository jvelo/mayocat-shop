/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.home.api.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.cms.home.model.HomePage
import org.mayocat.configuration.PlatformSettings
import org.mayocat.model.AddonGroup
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.theme.ThemeDefinition

import static org.mayocat.rest.api.object.AddonGroupApiObject.forAddonGroup
import static org.mayocat.rest.api.object.AddonGroupApiObject.toAddonGroupMap

/**
 * @version $Id$
 */
@CompileStatic
class HomePageApiObject extends BaseApiObject
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, AddonGroupApiObject> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<Locale, Map<String, Object>> _localized;

    @JsonIgnore
    def withHomePage(HomePage page)
    {
        _localized = page.localizedVersions
    }

    @JsonIgnore
    HomePage toHomePage(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition)
    {
        HomePage homePage = new HomePage()
        homePage.addons = toAddonGroupMap(addons, platformSettings, themeDefinition)
        homePage
    }

    @JsonIgnore
    def withAddons(Map<String, AddonGroup> entityAddons)
    {
        if (!addons) {
            addons = [:]
        }

        entityAddons.values().each({ AddonGroup addon ->
            addons.put(addon.group, forAddonGroup(addon))
        })
    }
}
