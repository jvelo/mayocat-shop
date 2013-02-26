package org.mayocat.shop.model.reference;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class EntityReference
{
    private EntityReference parent;

    private String type;

    private String slug;

    public EntityReference(String type, String slug, Optional<EntityReference> parent)
    {
        this.slug = slug;
        this.type = type;
        this.parent = parent.orNull();
    }

    public EntityReference getParent()
    {
        return parent;
    }

    public void setParent(EntityReference parent)
    {
        this.parent = parent;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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
