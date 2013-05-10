package org.mayocat.rest.representations;

/**
 * @version $Id$
 */
public class EntityReferenceRepresentation extends LinkRepresentation
{
    private String title;

    private String slug;

    public EntityReferenceRepresentation()
    {
        // No-arg constructor required for Jackson deserialization
        super();
    }

    public EntityReferenceRepresentation(String href, String slug, String title)
    {
        super(href);
        this.title = title;
        this.slug = slug;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSlug()
    {
        return slug;
    }
}
