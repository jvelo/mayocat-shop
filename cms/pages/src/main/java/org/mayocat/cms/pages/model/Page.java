/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.model;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AddonGroup;
import org.mayocat.model.Association;
import org.mayocat.model.Child;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.HasModel;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.Index;
import org.mayocat.model.annotation.LocalizedField;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class Page implements Entity, Localized, Child, HasAddons, HasModel, HasFeaturedImage
{
    private UUID id;

    private UUID parentId = null;

    @Index
    @NotNull
    @Size(min = 1)
    private String slug;

    @Index
    private Boolean published;

    @LocalizedField
    @Index
    @NotNull
    private String title;

    @LocalizedField
    @Index
    private String content;

    private Association<Map<String, AddonGroup>> addons = Association.notLoaded();

    private Optional<String> model = Optional.absent();

    private UUID featuredImageId;

    private Map<Locale, Map<String, Object>> localizedVersions;

    public Page()
    {
    }

    public Page(UUID id)
    {
        setId(id);
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

    public Boolean getPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    // //////////////////////////////////////////////

    @Override
    public UUID getParentId()
    {
        return this.parentId;
    }

    @Override
    public void setParentId(UUID id)
    {
        this.parentId = id;
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

    public void setModel(String model)
    {
        this.model = Optional.fromNullable(model);
    }

    @Override
    public Optional<String> getModel()
    {
        return this.model;
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
        final Page other = (Page) obj;

        return     Objects.equal(this.id, other.id)
                && Objects.equal(this.parentId, other.parentId)
                && Objects.equal(this.title, other.title)
                && Objects.equal(this.slug, other.slug)
                && Objects.equal(this.content, other.content)
                && Objects.equal(this.published, other.published)
                && Objects.equal(this.addons, other.addons);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.id,
                this.parentId,
                this.slug,
                this.title,
                this.content,
                this.published,
                this.addons
        );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(this.id)
                .addValue(this.title)
                .addValue(this.slug).toString();
    }
}
