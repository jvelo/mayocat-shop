package org.mayocat.shop.model;

import java.io.InputStream;

import org.mayocat.shop.model.reference.EntityReference;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class Attachment implements Entity
{
    private Long id;

    private String slug;

    private String title;

    private String extension;

    private InputStream data;

    private EntityReference parent;

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
        return new EntityReference("attachment", getSlug(), Optional.fromNullable(this.parent));
    }

    @Override
    public EntityReference getParentReference()
    {
        return this.parent;
    }

    public void setParent(EntityReference reference)
    {
        this.parent = parent;
    }
}
