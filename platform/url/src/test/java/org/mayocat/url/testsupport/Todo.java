package org.mayocat.url.testsupport;

import java.util.UUID;

import org.mayocat.model.Entity;
import org.mayocat.model.annotation.PluralForm;

/**
 * @version $Id$
 */
@PluralForm("todos")
public class Todo implements Entity
{
    private UUID id;

    private String slug;

    public Todo(UUID id, String slug)
    {
        this.id = id;
        this.slug = slug;
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
}
