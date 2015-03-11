/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.model;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.mayocat.image.model.Image;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.Association;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.annotation.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

public class Tenant implements Entity, HasAddons, HasFeaturedImage
{
    @JsonIgnore
    private UUID id;

    @JsonIgnore
    private UUID featuredImageId;

    @NotNull
    @Pattern(message = "Only word characters or hyphens", regexp = "\\w[\\w-]*\\w")
    private String slug;

    @Index
    @NotBlank
    private String name;

    private String description;

    private String contactEmail;

    private Date creationDate;

    // FIXME
    // Implement a @Hostname constraint that verifies a hostname is valid
    @Index
    private String defaultHost;

    @JsonIgnore
    private TenantConfiguration configuration;

    @Index
    private Association<Map<String, AddonGroup>> addons = Association.notLoaded();

    @JsonIgnore
    private Association<Image> featuredImage = Association.notLoaded();

    ///////////////////////////////////////////////////

    public Tenant()
    {
        this.configuration = new TenantConfiguration();
    }

    public Tenant(String slug, TenantConfiguration configuration)
    {
        setSlug(slug);
        this.configuration = configuration;
    }

    public Tenant(UUID id, String slug, TenantConfiguration configuration)
    {
        setId(id);
        setSlug(slug);
        this.configuration = configuration;
    }

    ///////////////////////////////////////////////////

    public TenantConfiguration getConfiguration()
    {
        return configuration;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public void setDefaultHost(String defaultHost)
    {
        this.defaultHost = defaultHost;
    }

    public String getDefaultHost()
    {
        return defaultHost;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    @Override
    public Association<Map<String, AddonGroup>> getAddons()
    {
        return addons;
    }

    @Override
    public void setAddons(Map<String, AddonGroup> addons)
    {
        this.addons = new Association(addons);
    }

    public UUID getFeaturedImageId()
    {
        return featuredImageId;
    }

    public void setFeaturedImageId(UUID featuredImageId)
    {
        this.featuredImageId = featuredImageId;
    }

    public Association<Image> getFeaturedImage()
    {
        return featuredImage;
    }

    public void setFeaturedImage(Image featuredImage)
    {
        this.featuredImage = new Association<Image>(featuredImage);
    }

    // ///////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tenant other = (Tenant) obj;

        return Objects.equal(this.slug, other.slug);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.slug);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.slug).toString();
    }
}
