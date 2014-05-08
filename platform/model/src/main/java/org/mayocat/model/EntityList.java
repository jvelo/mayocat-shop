/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.annotation.DoNotIndex;

import com.google.common.base.Objects;

/**
 * Model for a list of {@link Entity}, which are entities themselves.
 *
 * @version $Id$
 */
public class EntityList implements Entity, Child
{
    @DoNotIndex
    private UUID id;

    @DoNotIndex
    private UUID parentId;

    @NotNull
    @Size(min = 1)
    private String slug;

    private String type;

    private List<UUID> entities = new ArrayList<>();

    // FUTURE:
    // if we want to implement a "mixed" type (where entities in the list are of heterogeneous types), this list would
    // hold the individual types at the #entities list matching index.
    // private List<String> types;

    private String hint;

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
        return this.slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public List<UUID> getEntities()
    {
        return entities;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setEntities(List<UUID> entities)
    {
        this.entities = entities;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityList other = (EntityList) obj;

        return Objects.equal(this.id, other.id)
                && Objects.equal(this.hint, other.hint)
                && Objects.equal(this.entities, other.entities)
                && Objects.equal(this.slug, other.slug);
    }

    public int hashCode()
    {
        return Objects.hashCode(
                this.id,
                this.hint,
                this.entities,
                this.slug
        );
    }

    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(this.slug)
                .addValue(this.hint)
                .toString();
    }

    public UUID getParentId()
    {
        return parentId;
    }

    public void setParentId(UUID id)
    {
        this.parentId = id;
    }
}
