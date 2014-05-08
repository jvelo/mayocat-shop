/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch.internal.testsupport;

import java.util.UUID;

import org.mayocat.model.Entity;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.annotation.Index;

/**
* @version $Id$
*/
@Index
public class EntityWithFeaturedImage implements Entity, HasFeaturedImage
{
    private UUID id;

    private UUID featuredImageId;

    private String slug;

    public EntityWithFeaturedImage(UUID id, UUID featuredImageId, String slug)
    {
        this.id = id;
        this.featuredImageId = featuredImageId;
        this.slug = slug;
    }

    public UUID getFeaturedImageId()
    {
        return this.featuredImageId;
    }

    public void setFeaturedImageId(UUID id)
    {
        this.featuredImageId = id;
    }

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
        this.slug = slug;
    }
}
