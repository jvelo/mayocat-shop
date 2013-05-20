package org.mayocat.search.elasticsearch.internal.testsupport;

import java.util.UUID;

import org.mayocat.model.Entity;

/**
 * @version $Id$
 */
public class CustomEntity implements Entity
{
    private UUID id;

    private String slug;

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
}
