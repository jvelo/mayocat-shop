package org.mayocat.model;

import java.io.InputStream;
import java.util.UUID;

import org.mayocat.model.annotation.LocalizedField;

/**
 * @version $Id$
 */
public class Attachment implements Entity, Child
{
    private UUID id;

    private String slug;

    @LocalizedField
    private String title;

    private String description;

    private String extension;

    private InputStream data;

    private UUID parentId;

    public Attachment()
    {
    }

    @Override
    public String getSlug()
    {
        return this.slug;
    }

    @Override
    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    @Override
    public UUID getId()
    {
        return this.id;
    }

    @Override
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

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public InputStream getData()
    {
        return data;
    }

    public void setData(InputStream data)
    {
        this.data = data;
    }

    @Override
    public UUID getParentId()
    {
        return this.parentId;
    }

    public void setParentId(UUID parentId)
    {
        this.parentId = parentId;
    }

}
