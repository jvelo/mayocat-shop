package org.mayocat.shop.rest.representations;

/**
 * @version $Id$
 */
public class EntityReferenceRepresentation
{
    private String href;

    private String title;

    public EntityReferenceRepresentation(String uri, String title)
    {
        this.title = title;
        this.href = uri;
    }

    public String getTitle()
    {
        return title;
    }

    public String getHref()
    {
        return href;
    }
}
