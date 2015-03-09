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
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.model.AddonGroup
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.theme.ThemeDefinition

/**
 * API object for a tenant
 *
 * @version $Id$
 */
@CompileStatic
class TenantApiObject extends BaseApiObject
{
    String slug;

    String name;

    String description;

    String contactEmail;

    @JsonIgnore
    // Ignored on de-serialization only. See getter and setter
    DateTime creationDate;

    String defaultHost;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, AddonGroupApiObject> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<Locale, Map<String, Object>> _localized;

    @JsonProperty("creationDate")
    public DateTime getCreationDate()
    {
        return creationDate;
    }

    @JsonIgnore
    public void setCreationDate(DateTime creationDate)
    {
        this.creationDate = creationDate;
    }

    @JsonIgnore
    TenantApiObject withTenant(Tenant tenant, DateTimeZone timeZone)
    {
        slug = tenant.slug
        name = tenant.name
        description = tenant.description
        contactEmail = tenant.contactEmail
        defaultHost = tenant.defaultHost

        if (tenant.creationDate != null) {
            creationDate = new DateTime(tenant.creationDate.time, timeZone);
        }

        this
    }

    @JsonIgnore
    TenantApiObject withAddons(Map<String, AddonGroup> entityAddons)
    {
        if (!addons) {
            addons = [:]
        }

        entityAddons.values().each({ AddonGroup addon ->
            addons.put(addon.group, org.mayocat.rest.api.object.AddonGroupApiObject.forAddonGroup(addon))
        })

        this
    }

    @JsonIgnore
    TenantApiObject withEmbeddedImages(List<Image> images, UUID featuredImageId, String tenantPrefix)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        def List<ImageApiObject> imageApiObjectList = [];

        images.each({ Image image ->
            ImageApiObject imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image, tenantPrefix)
            imageApiObject.featured = false

            if (image.attachment.id == featuredImageId) {
                featuredImage = imageApiObject
                imageApiObject.featured = true
            }
            imageApiObjectList << imageApiObject
        })

        _embedded.images = imageApiObjectList;

        if (featuredImage) {
            _embedded.featuredImage = featuredImage
        }

        this
    }

    @JsonIgnore
    TenantApiObject withEmbeddedFeaturedImage(Image featuredImage, String tenantPrefix)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        def imageApiObject = new ImageApiObject()
        imageApiObject.withImage(featuredImage, tenantPrefix)
        imageApiObject.featured = true
        _embedded.featuredImage = imageApiObject

        this
    }

    @JsonIgnore
    Tenant toTenant(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition)
    {
        def tenant = new Tenant()
        tenant.with {
            name = this.name
            description = this.description
            contactEmail = this.contactEmail
            slug = this.slug
        }

        if (addons) {
            tenant.addons = org.mayocat.rest.api.object.AddonGroupApiObject.
                    toAddonGroupMap(addons, platformSettings, themeDefinition)
        }

        tenant
    }
}