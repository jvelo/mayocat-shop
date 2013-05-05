package org.mayocat.rest.representations;

/**
 * @version $Id$
 */
public class LinkRepresentation
{
    private String href;

    public LinkRepresentation()
    {
    }

    public LinkRepresentation(String href)
    {
        this.href = href;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }
}
