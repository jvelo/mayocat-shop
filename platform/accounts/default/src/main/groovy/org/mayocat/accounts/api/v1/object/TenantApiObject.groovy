/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.mayocat.accounts.model.Tenant
import org.mayocat.addons.api.representation.AddonRepresentation
import org.mayocat.addons.model.AddonField
import org.mayocat.addons.model.BaseProperties
import org.mayocat.addons.util.AddonUtils
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.model.Addon
import org.mayocat.rest.api.object.AddonApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.representations.ImageRepresentation
import org.mayocat.theme.ThemeDefinition

/**
 * API object for tenant
 *
 * See {@link org.mayocat.accounts.api.v1.TenantApi}
 *
 * @version $Id$
 */
@CompileStatic
class TenantApiObject extends BaseApiObject {

    String slug;

    String name;

    String description;

    String contactEmail;

    @JsonIgnore
    // Ignored on de-serialization only. See getter and setter
    DateTime creationDate;

    String defaultHost;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AddonApiObject> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonProperty("creationDate")
    public DateTime getCreationDate() {
        return creationDate;
    }

    @JsonIgnore
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @JsonIgnore
    def withTenant(Tenant tenant, DateTimeZone timeZone) {
        slug = tenant.slug
        name = tenant.name
        description = tenant.description
        contactEmail = tenant.contactEmail

        if (tenant.creationDate != null) {
            creationDate = new DateTime(tenant.creationDate.time, timeZone);
        }

    }

    @JsonIgnore
    def withAddons(List<Addon> productAddons) {
        if (!addons) {
            addons = []
        }

        productAddons.each({ Addon addon ->
            addons << AddonApiObject.forAddon(addon)
        })
    }

    @JsonIgnore
    def withEmbeddedImages(List<ImageApiObject> images) {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        images.each({ ImageApiObject image ->
            if (image.featured) {
                featuredImage = image
            }
        })

        _embedded.images = images;

        if (featuredImage) {
            _embedded.featuredImage = featuredImage
        }
    }

    @JsonIgnore
    def withEmbeddedFeaturedImage(Image featuredImage) {
        if (_embedded == null) {
            _embedded = [:]
        }

        def imageApiObject = new ImageApiObject()
        imageApiObject.withImage(featuredImage)
        imageApiObject.featured = true
        _embedded.featuredImage = imageApiObject
    }

    @JsonIgnore
    Tenant toTenant(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition) {
        def tenant = new Tenant()
        tenant.with {
            name = this.name
            description = this.description
            contactEmail = this.contactEmail
            slug = this.slug
        }

        if (addons) {
            List<Addon> tenantAddons = []
            addons.each({ AddonApiObject addon ->
                Addon productAddon = addon.toAddon()
                Optional<AddonField> definition = AddonUtils.findAddonDefinition(productAddon, platformSettings.addons)
                if (definition.isPresent() && !definition.get().properties.containsKey(BaseProperties.READ_ONLY)) {
                    // - Addons for which no definition can be found are ignored
                    // - Addons declared "Read only" are ignored : they can't be updated via this API !
                    tenantAddons << productAddon
                }
            })

            tenant.addons = tenantAddons
        }

        tenant
    }

}