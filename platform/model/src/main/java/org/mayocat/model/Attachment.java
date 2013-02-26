package org.mayocat.model;

import java.io.InputStream;

import org.mayocat.model.reference.EntityReference;

import com.google.common.base.Optional;

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

    private EntityReference reference;

    public Attachment()
    {
        this(null);
    }

    public Attachment(EntityReference parentReference)
    {
        this.reference = new EntityReference("category", getSlug(), Optional.<EntityReference>fromNullable(
                parentReference));
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
    public EntityReference getReference()
    {
        return this.reference;
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
