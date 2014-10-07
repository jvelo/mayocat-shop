/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.home.model;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AddonGroup;
import org.mayocat.model.Association;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.DoNotIndex;

import com.google.common.base.Objects;

/**
 * @version $Id$
 */
public class HomePage implements Entity, HasAddons, Localized
{
    @DoNotIndex
    private UUID id;

    @NotNull
    @Size(min = 1)
    private final String slug = "home";

    private Map<Locale, Map<String, Object>> localizedVersions;

    private Association<Map<String, AddonGroup>> addons = Association.notLoaded();

    public UUID getId()
    {
        return this.id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getSlug()
    {
        return this.slug;
    }

    public void setSlug(String slug)
    {
        throw new RuntimeException("Setting the slug of the home page is not supported");
    }

    public Map<Locale, Map<String, Object>> getLocalizedVersions()
    {
        return this.localizedVersions;
    }

    public void setLocalizedVersions(Map<Locale, Map<String, Object>> versions)
    {
        this.localizedVersions = versions;
    }

    public Association<Map<String, AddonGroup>> getAddons()
    {
        return addons;
    }

    public void setAddons(Map<String, AddonGroup> addons)
    {
        this.addons = new Association(addons);
    }

    ////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HomePage other = (HomePage) obj;

        return Objects.equal(this.id, other.id)
                && Objects.equal(this.slug, other.slug)
                && Objects.equal(this.addons, other.addons);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.slug,
                this.addons
        );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(this.slug)
                .toString();
    }
}
