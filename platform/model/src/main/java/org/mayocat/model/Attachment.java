package org.mayocat.model;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @version $Id$
 */
public class Attachment implements Entity, Child
{
    private Long id;

    private String slug;

    private String title;

    private String description;

    private String extension;

    private InputStream data;

    private Long parentId;

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
    public Long getId()
    {
        return this.id;
    }

    @Override
    public void setId(Long id)
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
    public Long getParentId()
    {
        return this.parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

}
