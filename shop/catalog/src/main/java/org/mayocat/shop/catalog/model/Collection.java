/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.model;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.Entity;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.Index;
import org.mayocat.model.annotation.LocalizationFieldType;
import org.mayocat.model.annotation.LocalizedField;

import com.google.common.base.Objects;

public class Collection implements Entity, HasFeaturedImage, Localized
{
    private UUID id;

    @Index
    @NotNull
    @Size(min = 1)
    private String slug;

    @LocalizedField
    @Index
    @NotNull
    private String title;

    @LocalizedField
    @Index
    private String description;

    private UUID featuredImageId;

    private Map<Locale, Map<String, Object>> localizedVersions;

    public Collection()
    {
        super();
    }

    public Collection(UUID id)
    {
        super();
        this.id = id;
    }

    public UUID getId()
    {
        return id;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public UUID getFeaturedImageId()
    {
        return featuredImageId;
    }

    public void setFeaturedImageId(UUID featuredImageId)
    {
        this.featuredImageId = featuredImageId;
    }


    public void setLocalizedVersions(Map<Locale, Map<String, Object>> versions)
    {
        this.localizedVersions = versions;
    }


    @Override
    public Map<Locale, Map<String, Object>> getLocalizedVersions()
    {
        return localizedVersions;
    }

    // //////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Collection other = (Collection) obj;

        return Objects.equal(this.id, other.id)
            && Objects.equal(this.title, other.title)
            && Objects.equal(this.slug, other.slug)
            && Objects.equal(this.description, other.description);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.slug, this.title, this.description);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.title).addValue(this.slug).toString();
    }
}
