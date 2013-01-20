package org.mayocat.shop.rest.representations;

/**
 * @version $Id$
 */
public class EntityReference
{
    private String href;

    private String title;

    public EntityReference(String title, String uri)
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
