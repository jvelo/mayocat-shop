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

import org.hibernate.validator.constraints.NotBlank;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.Association;
import org.mayocat.model.Child;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.DoNotIndex;
import org.mayocat.model.annotation.LocalizedField;

import com.google.common.base.Objects;

/**
 * Model for a product feature (for example: a size, a color, etc.)
 *
 * @version $Id$
 */
public class Feature implements Entity, HasAddons, Localized, Child
{
    @DoNotIndex
    private UUID id;

    @DoNotIndex
    private UUID parentId;

    @NotBlank
    @Size(min = 1)
    private String slug;

    @NotBlank
    private String feature;

    @NotBlank
    private String featureSlug;

    @LocalizedField
    @NotNull
    @Size(min = 1)
    private String title;

    private Association<Map<String, AddonGroup>> addons = Association.notLoaded();

    private Map<Locale, Map<String, Object>> localizedVersions;

    public Feature()
    {
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public UUID getParentId()
    {
        return parentId;
    }

    public void setParentId(UUID id)
    {
        this.parentId = id;
    }

    public String getFeature()
    {
        return feature;
    }

    public void setFeature(String feature)
    {
        this.feature = feature;
    }

    public String getFeatureSlug()
    {
        return featureSlug;
    }

    public void setFeatureSlug(String featureSlug)
    {
        this.featureSlug = featureSlug;
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

    //

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;

        return Objects.equal(this.id, other.id)
                && Objects.equal(this.parentId, other.parentId)
                && Objects.equal(this.title, other.title)
                && Objects.equal(this.slug, other.slug)
                && Objects.equal(this.featureSlug, other.featureSlug)
                && Objects.equal(this.feature, other.feature);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.id,
                this.parentId,
                this.slug,
                this.title,
                this.feature,
                this.featureSlug
        );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(this.title)
                .addValue(this.slug)
                .addValue(this.feature)
                .addValue(this.featureSlug)
                .toString();
    }
}
