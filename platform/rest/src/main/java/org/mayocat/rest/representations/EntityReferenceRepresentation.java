package org.mayocat.rest.representations;

/**
 * @version $Id$
 */
public class EntityReferenceRepresentation
{
    private String href;

    private String title;

    private String slug;

    public EntityReferenceRepresentation()
    {
        // No-arg constructor required for Jackson deserialization
    }

    public EntityReferenceRepresentation(String title, String slug, String uri)
    {
        this.title = title;
        this.href = uri;
        this.slug = slug;
    }

    public String getTitle()
    {
        return title;
    }

    public String getHref()
    {
        return href;
    }

    public String getSlug()
    {
        return slug;
    }
}
